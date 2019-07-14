package dmodel.pipeline.rt.pcm.usagemodel.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import dmodel.pipeline.dt.mmmodel.IAbstractUsageDescriptor;
import dmodel.pipeline.dt.mmmodel.UsageData;
import dmodel.pipeline.dt.mmmodel.UsageServiceCallDescriptor;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.IUsageDataExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.ServiceCallSession;
import dmodel.pipeline.rt.pcm.usagemodel.tree.path.IPathExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.transition.ITransitionTreeExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.transition.ReferenceTransitionTreeExtractor;
import dmodel.pipeline.shared.structure.Tree;
import dmodel.pipeline.shared.structure.Tree.TreeNode;

public class TreeBranchExtractor implements IUsageDataExtractor {

	private ITransitionTreeExtractor treeExtractor;
	private IPathExtractor pathExtractor;

	public TreeBranchExtractor() {
		this.treeExtractor = new ReferenceTransitionTreeExtractor();
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
				.extractRelevantPaths(treeWithoutLoops, 0.75f);

		// 4.3. compress paths
		// TODO

		// 5. build final groups
		// TODO

		return null;
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
