package cipm.consistency.runtime.pipeline.pcm.usagemodel.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import com.google.common.collect.Lists;

import cipm.consistency.base.core.config.ConfigurationContainer;
import cipm.consistency.base.core.facade.pcm.IRepositoryQueryFacade;
import cipm.consistency.base.core.facade.pcm.ISystemQueryFacade;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.base.shared.structure.Tree.TreeNode;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.IUsageDataExtractor;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.ServiceCallSession;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.clustering.DBScanUsageSessionClusterer;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.clustering.IUsageSessionClustering;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.IAbstractUsageDescriptor;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.UsageBranchDescriptor;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.UsageBranchTransition;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.UsageGroup;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.UsageServiceCallDescriptor;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.tree.transition.ITransitionTreeExtractor;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.tree.transition.ReferenceTransitionTreeExtractor;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.util.UsageServiceUtil;
import lombok.extern.java.Log;

@Log
public class TreeBranchExtractor implements IUsageDataExtractor {
	private static final double NANO_TO_MS = 1000000;

	private ITransitionTreeExtractor treeExtractor;
	private IUsageSessionClustering sessionClusterer;
	private TreeLoopShrinker treeShrinker;
	private TreeMerger treeMerger;

	private int currentGroupId;

	private ConfigurationContainer configuration;

	public TreeBranchExtractor(ConfigurationContainer configuration) {
		this.treeExtractor = new ReferenceTransitionTreeExtractor();
		this.sessionClusterer = new DBScanUsageSessionClusterer();
		this.treeShrinker = new TreeLoopShrinker();
		this.treeMerger = new TreeMerger();

		this.configuration = configuration;
	}

	@Override
	public List<UsageScenario> extract(List<Tree<ServiceCallRecord>> callSequences, IRepositoryQueryFacade repository,
			ISystemQueryFacade system) {
		// reset old
		currentGroupId = 0;
		// 1. create entry call tree
		log.info("Extract entry calls.");
		List<ServiceCallRecord> entryCalls = callSequences.stream().map(e -> e.getRoot().getData())
				.filter(e -> UsageServiceUtil.isEntryCall(repository, system, e)).collect(Collectors.toList());

		if (entryCalls.size() < 1) {
			return Lists.newArrayList();
		}

		// 2. extract user sessions
		log.info("Extract sessions.");
		List<ServiceCallSession> sessions = extractSessions(entryCalls);

		int sessionNumber = sessions.size();
		long lowest = entryCalls.stream().map(c -> c.getEntryTime()).min(Long::compare).get();
		long highest = entryCalls.stream().map(c -> c.getEntryTime()).max(Long::compare).get();
		double interarrivalOverall = ((highest - lowest) / NANO_TO_MS) / ((double) sessionNumber);

		// 2.1 cluster sessions
		List<List<ServiceCallSession>> clusters = sessionClusterer.clusterSessions(sessions);

		// 3. create probability tree
		log.info("Extract trees.");
		List<Tree<DescriptorTransition<UsageServiceCallDescriptor>>> transitionTrees = clusters.stream()
				.map(c -> treeExtractor.extractProbabilityCallTree(c, repository, system)).collect(Collectors.toList());

		// 4. find loop structures
		// 4.1. bundle consecutive identical calls
		log.info("Identify paths.");

		List<Tree<DescriptorTransition<IAbstractUsageDescriptor>>> subTrees = Lists.newArrayList();
		for (Tree<DescriptorTransition<UsageServiceCallDescriptor>> transitionTree : transitionTrees) {
			Tree<DescriptorTransition<IAbstractUsageDescriptor>> treeWithoutLoops = new Tree<>(
					new DescriptorTransition<>(transitionTree.getRoot().getData().getCall(), 1.0f));

			copyTree(treeWithoutLoops.getRoot(), transitionTree.getRoot());
			treeShrinker.shrinkTree(treeWithoutLoops);

			subTrees.add(treeWithoutLoops);
		}

		// 4.2. merge trees that are mergeable (identical structure)
		int currentIndexA = 0;
		int currentIndexB = 1;
		while (currentIndexA < subTrees.size() - 1) {
			while (currentIndexB <= subTrees.size() - 1) {
				boolean merged = treeMerger.mergeTrees(subTrees.get(currentIndexA), subTrees.get(currentIndexB));
				if (merged) {
					subTrees.remove(currentIndexB);
				} else {
					currentIndexB++;
				}
			}
			currentIndexA++;
			currentIndexB = currentIndexA + 1;
		}

		// 4.3. filter out not relevant trees
		// TODO

		// 5. build final groups
		log.info("Finalize usage scenarios.");
		return IntStream.range(0, subTrees.size()).mapToObj(i -> {
			Tree<DescriptorTransition<IAbstractUsageDescriptor>> relevantTree = subTrees.get(i);
			List<ServiceCallSession> correspondingCluster = clusters.get(i);

			if (relevantTree.getRoot().getChildren().size() > 0) {
				double interarrival = interarrivalOverall
						/ ((double) correspondingCluster.size() / (double) sessionNumber);
				if (interarrival < configuration.getVfl().getMinInterarrivalTime()) {
					interarrival = configuration.getVfl().getMinInterarrivalTime();
				}

				UsageGroup usageGroup = buildUserGroup(relevantTree, interarrival);
				return usageGroup.toPCM();
			}

			return null;
		}).filter(f -> f != null).collect(Collectors.toList());
	}

	private synchronized UsageGroup buildUserGroup(Tree<DescriptorTransition<IAbstractUsageDescriptor>> relevantTree,
			double interarr) {
		UsageGroup nGroup = new UsageGroup(currentGroupId++, interarr);
		buildUserGroupRecursive(nGroup.getDescriptors(), relevantTree.getRoot().getChildren());
		return nGroup;
	}

	private void buildUserGroupRecursive(List<IAbstractUsageDescriptor> container,
			List<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>> childs) {
		Stack<Pair<List<IAbstractUsageDescriptor>, List<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>>>> nodeStack = new Stack<>();
		nodeStack.add(Pair.of(container, childs));

		while (!nodeStack.empty()) {
			Pair<List<IAbstractUsageDescriptor>, List<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>>> current = nodeStack
					.pop();
			List<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>> childsInner = current.getRight();
			List<IAbstractUsageDescriptor> containerInner = current.getLeft();

			if (childsInner.size() == 1) {
				TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> child = childsInner.get(0);
				// straight forward -> add it and start recursion
				containerInner.add(child.getData().getCall());
				nodeStack.add(Pair.of(container, child.getChildren()));
			} else if (childsInner.size() > 1) {
				// we need to create a branch
				UsageBranchDescriptor nBranch = new UsageBranchDescriptor();

				// renormalize the branch prob
				double sumProb = childsInner.stream().mapToDouble(c -> c.getData().getProbability()).sum();
				double scale = 1.0d / sumProb;
				for (TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> child : childsInner) {
					UsageBranchTransition nTransition = new UsageBranchTransition();
					nTransition.setProbability(scale * child.getData().getProbability());

					nodeStack.add(Pair.of(nTransition.getChilds(), child.getChildren()));
				}

				// finally add the branch
				containerInner.add(nBranch);
			}
		}
	}

	private void copyTree(TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> to,
			TreeNode<DescriptorTransition<UsageServiceCallDescriptor>> from) {
		Stack<Pair<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>, TreeNode<DescriptorTransition<UsageServiceCallDescriptor>>>> nodeStack = new Stack<>();
		nodeStack.add(Pair.of(to, from));

		while (!nodeStack.empty()) {
			Pair<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>, TreeNode<DescriptorTransition<UsageServiceCallDescriptor>>> current = nodeStack
					.pop();
			TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> toInner = current.getLeft();
			TreeNode<DescriptorTransition<UsageServiceCallDescriptor>> fromInner = current.getRight();

			for (TreeNode<DescriptorTransition<UsageServiceCallDescriptor>> child : fromInner.getChildren()) {
				TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> toInnerNew = toInner.addChildren(
						new DescriptorTransition<>(child.getData().getCall(), child.getData().getProbability()));
				nodeStack.add(Pair.of(toInnerNew, child));
			}
		}
	}

	private List<ServiceCallSession> extractSessions(List<ServiceCallRecord> entryCalls) {
		Map<String, List<ServiceCallRecord>> sessionMapping = new HashMap<>();
		for (ServiceCallRecord entryCall : entryCalls) {
			if (!sessionMapping.containsKey(entryCall.getSessionId())) {
				sessionMapping.put(entryCall.getSessionId(), Lists.newArrayList());
			}
			sessionMapping.get(entryCall.getSessionId()).add(entryCall);
		}

		return sessionMapping.entrySet().stream().map(e -> new ServiceCallSession(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}

}
