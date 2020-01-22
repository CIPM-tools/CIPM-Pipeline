package dmodel.pipeline.rt.pcm.usagemodel.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import com.google.common.collect.Lists;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.IUsageDataExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.ServiceCallSession;
import dmodel.pipeline.rt.pcm.usagemodel.data.IAbstractUsageDescriptor;
import dmodel.pipeline.rt.pcm.usagemodel.data.UsageBranchDescriptor;
import dmodel.pipeline.rt.pcm.usagemodel.data.UsageBranchTransition;
import dmodel.pipeline.rt.pcm.usagemodel.data.UsageGroup;
import dmodel.pipeline.rt.pcm.usagemodel.data.UsageServiceCallDescriptor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.path.IPathExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.path.SimpleComparisonPathExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.transition.ITransitionTreeExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.transition.ReferenceTransitionTreeExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.util.UsageServiceUtil;
import dmodel.pipeline.shared.structure.Tree;
import dmodel.pipeline.shared.structure.Tree.TreeNode;
import lombok.extern.java.Log;

@Log
public class TreeBranchExtractor implements IUsageDataExtractor {
	private static final double MIN_RELEVANCE = 0.05;
	private static final float USER_GROUP_MAX_VARIANCE = 0.25f;
	private static final double NANO_TO_MS = 1000000;

	private ITransitionTreeExtractor treeExtractor;
	private IPathExtractor pathExtractor;

	private int currentGroupId;

	public TreeBranchExtractor() {
		this.treeExtractor = new ReferenceTransitionTreeExtractor();
		this.pathExtractor = new SimpleComparisonPathExtractor();
	}

	@Override
	public List<UsageScenario> extract(List<Tree<ServiceCallRecord>> callSequences, Repository repository,
			System system) {
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

		// 3. create probability tree
		log.info("Extract tree.");
		Tree<DescriptorTransition<UsageServiceCallDescriptor>> transitionTree = treeExtractor
				.extractProbabilityCallTree(sessions, repository, system);

		// 4. find loop structures
		// 4.1. bundle consecutive identical calls
		log.info("Identify paths.");
		Tree<DescriptorTransition<IAbstractUsageDescriptor>> treeWithoutLoops = new Tree<>(
				new DescriptorTransition<>(transitionTree.getRoot().getData().getCall(), 1.0f));
		copyTree(treeWithoutLoops.getRoot(), transitionTree.getRoot());

		// 4.2. identify all paths through tree
		List<Tree<DescriptorTransition<IAbstractUsageDescriptor>>> subTrees = pathExtractor
				.extractRelevantPaths(treeWithoutLoops, (1.0f - USER_GROUP_MAX_VARIANCE));

		// 4.3. compress paths
		// makes in a lot of cases no sense (because of the parameters)

		// 4.4. collect relevant paths
		log.info("Collect relevant paths.");
		List<Tree<DescriptorTransition<IAbstractUsageDescriptor>>> relevantPaths = subTrees.stream()
				.filter(p -> estimateRelevance(p) >= MIN_RELEVANCE).collect(Collectors.toList());

		// 5. build final groups
		log.info("Finalize usage scenarios.");
		return relevantPaths.stream().map(relevantTree -> {
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
