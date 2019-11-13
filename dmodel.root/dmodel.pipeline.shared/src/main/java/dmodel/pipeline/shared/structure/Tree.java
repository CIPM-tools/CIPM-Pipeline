package dmodel.pipeline.shared.structure;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public class Tree<T> {
	private TreeNode<T> root;

	public Tree(T rootData) {
		root = new TreeNode<T>();
		root.setData(rootData);
	}

	public Tree(TreeNode<T> root) {
		this.root = root;
		this.root.setParent(null);
	}

	public TreeNode<T> getRoot() {
		return root;
	}

	@Data
	public static class TreeNode<T> {
		private T data;
		private TreeNode<T> parent;
		private List<TreeNode<T>> children;

		public TreeNode() {
			this.children = new ArrayList<>();
		}

		public TreeNode<T> addChildren(T data) {
			TreeNode<T> child = new TreeNode<T>();
			child.setData(data);
			child.setParent(this);
			this.children.add(child);

			return child;
		}

		public void addChildren(TreeNode<T> child) {
			child.setParent(this);
			this.children.add(child);
		}
	}
}