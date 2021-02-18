package cipm.consistency.runtime.pipeline.pcm.usagemodel.tree;

import java.util.Stack;

import com.google.common.collect.Lists;

import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.base.shared.structure.Tree.TreeNode;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.IAbstractUsageDescriptor;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.UsageLoopDescriptor;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.UsageServiceCallDescriptor;

public class TreeLoopShrinker {

	public void shrinkTree(Tree<DescriptorTransition<IAbstractUsageDescriptor>> treeWithLoops) {
		shrinkTreeRecursive(treeWithLoops.getRoot());
	}

	private void shrinkTreeRecursive(TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> node) {
		Stack<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>> currentStack = new Stack<>();
		currentStack.add(node);

		while (!currentStack.isEmpty()) {
			handleNode(currentStack.pop(), currentStack);
		}
	}

	private void handleNode(TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> node,
			Stack<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>> currentStack) {
		if (node.getChildren().size() == 1) {
			if (node.getData().getCall() == null) {
				// root
				currentStack.add(node.getChildren().get(0));
				return;
			}

			// check if same call
			TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> child = node.getChildren().get(0);
			if (child.getData().getCall() instanceof UsageServiceCallDescriptor
					&& node.getData().getCall() instanceof UsageServiceCallDescriptor) {
				// check if mergeable
				UsageServiceCallDescriptor parentCall = (UsageServiceCallDescriptor) node.getData().getCall();
				UsageServiceCallDescriptor childCall = (UsageServiceCallDescriptor) child.getData().getCall();

				if (parentCall.matches(childCall)) {
					// => mergeable

					// 1. merge
					parentCall.merge(childCall);

					// 2. wrap with loop
					UsageLoopDescriptor loopDescriptor = new UsageLoopDescriptor();
					loopDescriptor.setChilds(Lists.newArrayList(parentCall));
					loopDescriptor.setIterations(Lists.newArrayList(2));

					// 3. replace parent node with new descriptor
					node.getData().setCall(loopDescriptor);
					node.getChildren().clear();
					node.getChildren().addAll(child.getChildren());

					// 4. recursive continue
					currentStack.add(node);
				}
			} else if (child.getData().getCall() instanceof UsageServiceCallDescriptor
					&& node.getData().getCall() instanceof UsageLoopDescriptor) {
				// check if mergeable
				UsageLoopDescriptor parentCall = (UsageLoopDescriptor) node.getData().getCall();
				UsageServiceCallDescriptor childCall = (UsageServiceCallDescriptor) child.getData().getCall();

				if (parentCall.getChilds().size() == 1
						&& parentCall.getChilds().get(0) instanceof UsageServiceCallDescriptor) {
					UsageServiceCallDescriptor innerCall = (UsageServiceCallDescriptor) parentCall.getChilds().get(0);

					// merge
					innerCall.merge(childCall);
					parentCall.getIterations().add(parentCall.getIterations().get(0) + 1);
					parentCall.getIterations().remove(0);

					// replace childs
					node.getChildren().clear();
					node.getChildren().addAll(child.getChildren());

					// recursive continue
					currentStack.add(node);
				}
			}

			currentStack.add(child);
		} else {
			node.getChildren().forEach(c -> currentStack.add(c));
		}
	}

}
