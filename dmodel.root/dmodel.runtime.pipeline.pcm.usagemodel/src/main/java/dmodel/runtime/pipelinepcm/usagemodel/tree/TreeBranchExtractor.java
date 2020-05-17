package dmodel.runtime.pipelinepcm.usagemodel.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import com.google.common.collect.Lists;

import dmodel.base.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.base.core.facade.pcm.ISystemQueryFacade;
import dmodel.base.shared.structure.Tree;
import dmodel.base.shared.structure.Tree.TreeNode;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.runtime.pipelinepcm.usagemodel.IUsageDataExtractor;
import dmodel.runtime.pipelinepcm.usagemodel.ServiceCallSession;
import dmodel.runtime.pipelinepcm.usagemodel.clustering.DBScanUsageSessionClusterer;
import dmodel.runtime.pipelinepcm.usagemodel.clustering.IUsageSessionClustering;
import dmodel.runtime.pipelinepcm.usagemodel.data.IAbstractUsageDescriptor;
import dmodel.runtime.pipelinepcm.usagemodel.data.UsageBranchDescriptor;
import dmodel.runtime.pipelinepcm.usagemodel.data.UsageBranchTransition;
import dmodel.runtime.pipelinepcm.usagemodel.data.UsageGroup;
import dmodel.runtime.pipelinepcm.usagemodel.data.UsageServiceCallDescriptor;
import dmodel.runtime.pipelinepcm.usagemodel.tree.transition.ITransitionTreeExtractor;
import dmodel.runtime.pipelinepcm.usagemodel.tree.transition.ReferenceTransitionTreeExtractor;
import dmodel.runtime.pipelinepcm.usagemodel.util.UsageServiceUtil;
import lombok.extern.java.Log;

@Log
public class TreeBranchExtractor implements IUsageDataExtractor {
	private static final double MIN_RELEVANCE = 0.05;
	private static final double NANO_TO_MS = 1000000;

	private ITransitionTreeExtractor treeExtractor;
	private IUsageSessionClustering sessionClusterer;

	private int currentGroupId;

	public TreeBranchExtractor() {
		this.treeExtractor = new ReferenceTransitionTreeExtractor();
		this.sessionClusterer = new DBScanUsageSessionClusterer();
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
			subTrees.add(treeWithoutLoops);
		}

		// 4.2. filter out not relevant trees
		// TODO

		// 5. build final groups
		log.info("Finalize usage scenarios.");
		return subTrees.stream().map(relevantTree -> {
			if (relevantTree.getRoot().getChildren().size() > 0) {
				double relevance = estimateRelevance(relevantTree);
				double interarrival = interarrivalOverall / relevance;

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
		if (childs.size() == 1) {
			TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> child = childs.get(0);
			// straight forward -> add it and start recursion
			container.add(child.getData().getCall());
			buildUserGroupRecursive(container, child.getChildren());
		} else if (childs.size() > 1) {
			// we need to create a branch
			UsageBranchDescriptor nBranch = new UsageBranchDescriptor();

			// renormalize the branch prob
			double sumProb = childs.stream().mapToDouble(c -> c.getData().getProbability()).sum();
			double scale = 1.0d / sumProb;
			for (TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> child : childs) {
				UsageBranchTransition nTransition = new UsageBranchTransition();
				nTransition.setProbability(scale * child.getData().getProbability());
				buildUserGroupRecursive(nTransition.getChilds(), child.getChildren());
			}

			// finally add the branch
			container.add(nBranch);
		}
	}

	private double estimateRelevance(Tree<DescriptorTransition<IAbstractUsageDescriptor>> tree) {
		return estimateRelevanceRecursive(tree.getRoot());
	}

	private double estimateRelevanceRecursive(TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> node) {
		if (node.getChildren().size() == 0) {
			return node.getData().getProbability();
		} else {
			double sum = 0;
			for (TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> desc : node.getChildren()) {
				sum += estimateRelevanceRecursive(desc);
			}
			return sum;
		}
	}

	private void copyTree(TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> to,
			TreeNode<DescriptorTransition<UsageServiceCallDescriptor>> from) {
		for (TreeNode<DescriptorTransition<UsageServiceCallDescriptor>> child : from.getChildren()) {
			copyTree(
					to.addChildren(
							new DescriptorTransition<>(child.getData().getCall(), child.getData().getProbability())),
					child);
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
