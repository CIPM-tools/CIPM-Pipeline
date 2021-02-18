package cipm.consistency.runtime.pipeline.pcm.usagemodel.tree.transition;

import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;

import cipm.consistency.base.core.facade.pcm.IRepositoryQueryFacade;
import cipm.consistency.base.core.facade.pcm.ISystemQueryFacade;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.base.shared.structure.Tree.TreeNode;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.ServiceCallSession;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.UsageServiceCallDescriptor;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.tree.DescriptorTransition;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.util.UsageServiceUtil;
import lombok.extern.java.Log;

@Log
public class ReferenceTransitionTreeExtractor implements ITransitionTreeExtractor {
	@Override
	public Tree<DescriptorTransition<UsageServiceCallDescriptor>> extractProbabilityCallTree(
			List<ServiceCallSession> sessions, IRepositoryQueryFacade repository, ISystemQueryFacade system) {
		// 1. first build our internal representation tree
		Tree<ReferenceTreeTransition> innerTree = new Tree<>(new ReferenceTreeTransition(null));

		// 1.1. iterate over sessions
		for (ServiceCallSession session : sessions) {
			// 1.2. sort after entry time
			session.getEntryCalls().sort(UsageServiceUtil.COMPARE_SCAL_ENTRY_TIME_ASCENDING);

			// 1.3. enrich our tree
			innerTree.getRoot().getData().incrementTrack(); // increment root

			// 1.3.1. go down our tree
			TreeNode<ReferenceTreeTransition> currentNode = innerTree.getRoot();
			for (ServiceCallRecord next : session.getEntryCalls()) {
				// 1.3.2. convert the record
				UsageServiceCallDescriptor conv = UsageServiceUtil.createDescriptor(next, repository, system);

				// 1.3.3. already has the following node?
				TreeNode<ReferenceTreeTransition> nextNode = null;
				if (currentNode != null) {
					for (TreeNode<ReferenceTreeTransition> transition : currentNode.getChildren()) {
						if (transition.getData().callDescriptor.getServiceId().equals(conv.getServiceId())) {
							// this is our next node
							nextNode = transition;
							break;
						}
					}
				}

				// 1.3.4. we found a next one?
				TreeNode<ReferenceTreeTransition> currentPathNode;
				if (nextNode != null) {
					// inner merge
					nextNode.getData().merge(conv);

					// increase track
					nextNode.getData().incrementTrack();

					// set next
					currentPathNode = nextNode;
				} else {
					// -> create a new child node
					ReferenceTreeTransition childNode = new ReferenceTreeTransition(conv);
					childNode.incrementTrack(); // one time executed

					// add it
					currentPathNode = currentNode.addChildren(childNode);
				}

				// 1.3.5. set new node
				currentNode = currentPathNode;
			}
		}

		// 2. convert it to the transition tree by calculating probabilities
		Tree<DescriptorTransition<UsageServiceCallDescriptor>> transitionTree = new Tree<>(
				new DescriptorTransition<>(null, 1.0f));
		recursiveTreeConversion(transitionTree.getRoot(), innerTree.getRoot());

		return transitionTree;
	}

	private void recursiveTreeConversion(TreeNode<DescriptorTransition<UsageServiceCallDescriptor>> into,
			TreeNode<ReferenceTreeTransition> from) {
		Stack<Pair<TreeNode<DescriptorTransition<UsageServiceCallDescriptor>>, TreeNode<ReferenceTreeTransition>>> nodeStack = new Stack<>();
		nodeStack.add(Pair.of(into, from));

		while (!nodeStack.isEmpty()) {
			Pair<TreeNode<DescriptorTransition<UsageServiceCallDescriptor>>, TreeNode<ReferenceTreeTransition>> current = nodeStack
					.pop();
			TreeNode<DescriptorTransition<UsageServiceCallDescriptor>> intoInner = current.getLeft();
			TreeNode<ReferenceTreeTransition> fromInner = current.getRight();

			float parentTracks = fromInner.getData().tracked;
			for (TreeNode<ReferenceTreeTransition> child : fromInner.getChildren()) {
				float probability = ((float) child.getData().tracked) / parentTracks;
				DescriptorTransition<UsageServiceCallDescriptor> copy = new DescriptorTransition<>(
						child.getData().callDescriptor, probability);

				TreeNode<DescriptorTransition<UsageServiceCallDescriptor>> transition = intoInner.addChildren(copy);

				nodeStack.add(Pair.of(transition, child));
			}
		}
	}

	private class ReferenceTreeTransition {
		private UsageServiceCallDescriptor callDescriptor;
		private int tracked;

		public ReferenceTreeTransition(UsageServiceCallDescriptor conv) {
			callDescriptor = conv;
			tracked = 0;
		}

		private void merge(UsageServiceCallDescriptor other) {
			this.callDescriptor.merge(other);
		}

		private void incrementTrack() {
			tracked++;
		}
	}

}
