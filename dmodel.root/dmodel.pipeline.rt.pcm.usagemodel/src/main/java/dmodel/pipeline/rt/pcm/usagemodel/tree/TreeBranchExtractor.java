package dmodel.pipeline.rt.pcm.usagemodel.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import dmodel.pipeline.dt.mmmodel.IAbstractUsageDescriptor;
import dmodel.pipeline.dt.mmmodel.MmmodelFactory;
import dmodel.pipeline.dt.mmmodel.UsageBranchDescriptor;
import dmodel.pipeline.dt.mmmodel.UsageBranchTransition;
import dmodel.pipeline.dt.mmmodel.UsageData;
import dmodel.pipeline.dt.mmmodel.UsageGroup;
import dmodel.pipeline.dt.mmmodel.UsageServiceCallDescriptor;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.IUsageDataExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.ServiceCallSession;
import dmodel.pipeline.rt.pcm.usagemodel.tree.path.IPathExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.path.SimpleComparisonPathExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.transition.ITransitionTreeExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.transition.ReferenceTransitionTreeExtractor;
import dmodel.pipeline.shared.structure.Tree;
import dmodel.pipeline.shared.structure.Tree.TreeNode;

public class TreeBranchExtractor implements IUsageDataExtractor {
	private static final double MIN_RELEVANCE = 0.05;
	private static final float USER_GROUP_MAX_VARIANCE = 0.25f;

	private ITransitionTreeExtractor treeExtractor;
	private IPathExtractor pathExtractor;

	public TreeBranchExtractor() {
		this.treeExtractor = new ReferenceTransitionTreeExtractor();
		this.pathExtractor = new SimpleComparisonPathExtractor();
	}

	// TODO it is not absolutely necessary to build a specific tree and convert it
	// then to a more specific one
	// maybe we remove this later
	@Override
	public UsageData extract(List<Tree<ServiceCallRecord>> callSequences) {
		// 1. create entry call tree
		List<ServiceCallRecord> entryCalls = callSequences.parallelStream().map(e -> e.getRoot().getData())
				.collect(Collectors.toList());

		// 2. extract user sessions
		List<ServiceCallSession> sessions = extractSessions(entryCalls);

		// 3. create probability tree
		Tree<DescriptorTransition<UsageServiceCallDescriptor>> transitionTree = treeExtractor
				.extractProbabilityCallTree(sessions);

		// 4. find loop structures
		// 4.1. bundle consecutive identical calls
		Tree<DescriptorTransition<IAbstractUsageDescriptor>> treeWithoutLoops = new Tree<>(
				new DescriptorTransition<>(transitionTree.getRoot().getData().getCall(), 1.0f));
		copyTree(treeWithoutLoops.getRoot(), transitionTree.getRoot());

		// 4.2. identify all paths through tree
		List<Tree<DescriptorTransition<IAbstractUsageDescriptor>>> subTrees = pathExtractor
				.extractRelevantPaths(treeWithoutLoops, (1.0f - USER_GROUP_MAX_VARIANCE));

		// 4.3. compress paths
		// TODO

		// 4.4. collect relevant paths
		List<Tree<DescriptorTransition<IAbstractUsageDescriptor>>> relevantPaths = subTrees.stream()
				.filter(p -> estimateRelevance(p) >= MIN_RELEVANCE).collect(Collectors.toList());

		// 5. build final groups
		UsageData usageData = MmmodelFactory.eINSTANCE.createUsageData();
		for (Tree<DescriptorTransition<IAbstractUsageDescriptor>> relevantTree : relevantPaths) {
			if (relevantTree.getRoot().getChildren().size() > 0) {
				UsageGroup usageGroup = buildUserGroup(relevantTree);
				usageData.getGroups().add(usageGroup);
			}
		}

		return usageData;
	}

	private UsageGroup buildUserGroup(Tree<DescriptorTransition<IAbstractUsageDescriptor>> relevantTree) {
		UsageGroup nGroup = MmmodelFactory.eINSTANCE.createUsageGroup();

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
		} else {
			// we need to create a branch
			UsageBranchDescriptor nBranch = MmmodelFactory.eINSTANCE.createUsageBranchDescriptor();

			// renormalize the branch prob
			double sumProb = childs.stream().mapToDouble(c -> c.getData().getProbability()).sum();
			double scale = 1.0d / sumProb;
			for (TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> child : childs) {
				UsageBranchTransition nTransition = MmmodelFactory.eINSTANCE.createUsageBranchTransition();
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
		return sessionMapping.entrySet().parallelStream().map(e -> new ServiceCallSession(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}

}