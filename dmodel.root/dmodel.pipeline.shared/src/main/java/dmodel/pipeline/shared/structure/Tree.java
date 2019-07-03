package dmodel.pipeline.shared.structure;

import java.util.ArrayList;
import java.util.List;

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

	public static class TreeNode<T> {
		private T data;
		private TreeNode<T> parent;
		private List<TreeNode<T>> children;

		public TreeNode() {
			this.children = new ArrayList<>();
		}

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public TreeNode<T> getParent() {
			return parent;
		}

		public void setParent(TreeNode<T> parent) {
			this.parent = parent;
		}

		public List<TreeNode<T>> getChildren() {
			return children;
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