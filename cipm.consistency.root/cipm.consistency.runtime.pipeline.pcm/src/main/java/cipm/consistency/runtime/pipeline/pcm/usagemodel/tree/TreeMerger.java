package cipm.consistency.runtime.pipeline.pcm.usagemodel.tree;

import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.base.shared.structure.Tree.TreeNode;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.IAbstractUsageDescriptor;

public class TreeMerger {

	public boolean mergeTrees(Tree<DescriptorTransition<IAbstractUsageDescriptor>> treeA,
			Tree<DescriptorTransition<IAbstractUsageDescriptor>> treeB) {

		if (isMergable(treeA.getRoot(), treeB.getRoot())) {
			merge(treeA.getRoot(), treeB.getRoot());

			return true;
		}

		return false;
	}

	private void merge(TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> root,
			TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> root2) {
		for (int i = 0; i < root.getChildren().size(); i++) {
			IAbstractUsageDescriptor desc1 = root.getChildren().get(i).getData().getCall();
			IAbstractUsageDescriptor desc2 = root2.getChildren().get(i).getData().getCall();

			desc1.merge(desc2);
			merge(root.getChildren().get(i), root2.getChildren().get(i));
		}
	}

	private boolean isMergable(TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> root,
			TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> root2) {

		if (root.getChildren().size() == root2.getChildren().size()) {
			for (int i = 0; i < root.getChildren().size(); i++) {
				IAbstractUsageDescriptor desc1 = root.getChildren().get(i).getData().getCall();
				IAbstractUsageDescriptor desc2 = root2.getChildren().get(i).getData().getCall();

				if (desc1.matches(desc2)) {
					if (!isMergable(root.getChildren().get(i), root2.getChildren().get(i))) {
						return false;
					}
				} else {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

}
