package dmodel.pipeline.rt.pcm.repository.tree;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TreeNode<T> {

	public T data;
	public TreeNode<T> parent;
	public List<TreeNode<T>> children;

	public TreeNode(T data) {
		this.data = data;
		this.children = new LinkedList<TreeNode<T>>();
	}

	public TreeNode<T> addChild(T child) {
		TreeNode<T> childNode = new TreeNode<T>(child);
		childNode.parent = this;
		this.children.add(childNode);
		return childNode;
	}

	@Override
	public String toString() {
		return innerToString(0);
	}

	private String innerToString(int indent) {
		StringBuilder builder = new StringBuilder();
		String front = StringUtils.repeat(" ", indent);
		builder.append(front + "-> " + data.toString());
		for (TreeNode<T> child : children) {
			builder.append("\n" + child.innerToString(indent + 2));
		}
		return builder.toString();
	}

}