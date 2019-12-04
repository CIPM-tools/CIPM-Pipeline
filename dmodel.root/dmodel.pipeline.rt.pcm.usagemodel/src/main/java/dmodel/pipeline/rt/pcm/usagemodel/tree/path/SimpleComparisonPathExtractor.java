package dmodel.pipeline.rt.pcm.usagemodel.tree.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import dmodel.pipeline.rt.pcm.usagemodel.data.IAbstractUsageDescriptor;
import dmodel.pipeline.rt.pcm.usagemodel.data.UsageServiceCallDescriptor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.DescriptorTransition;
import dmodel.pipeline.shared.structure.Tree;
import dmodel.pipeline.shared.structure.Tree.TreeNode;

// TODO refactor -> outsource into methods etc.
public class SimpleComparisonPathExtractor implements IPathExtractor {

	@Override
	public List<Tree<DescriptorTransition<IAbstractUsageDescriptor>>> extractRelevantPaths(
			Tree<DescriptorTransition<IAbstractUsageDescriptor>> tree, float subSimilarityThres) {
		List<Tree<DescriptorTransition<IAbstractUsageDescriptor>>> results = new ArrayList<>();

		Tree<DescriptorTransition<IAbstractUsageDescriptor>> nTree = new Tree<>(tree.getRoot().getData());
		recursiveExtractPaths(tree.getRoot(), nTree.getRoot(), nTree, results, subSimilarityThres);

		return results;
	}

	private void recursiveExtractPaths(TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> currentNode,
			TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> currentTreeNode,
			Tree<DescriptorTransition<IAbstractUsageDescriptor>> currentTree,
			List<Tree<DescriptorTransition<IAbstractUsageDescriptor>>> container, float threshold) {

		if (currentNode.getChildren().size() > 1) {
			List<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>> children = currentNode.getChildren();
			// we need to compare the sub branches
			double[][] similarityTable = new double[children.size()][children.size()];
			for (int i = 0; i < children.size(); i++) {
				for (int j = 0; j < children.size(); j++) {
					if (i == j) {
						similarityTable[i][j] = 1;
					} else if (i > j) {
						// we already got his
						similarityTable[i][j] = similarityTable[j][i];
					} else {
						// calculate
						similarityTable[i][j] = calculateSimilarity(children.get(i), children.get(j));
					}
				}
			}

			// calculate the max similarity set
			Set<Integer> mergeable = new HashSet<>();
			boolean modified = true;
			while (modified) {
				int iMax = -1, jMax = -1;
				for (int i = 0; i < children.size(); i++) {
					for (int j = 0; j < children.size(); j++) {
						if (j > i) {
							if (iMax >= 0 && jMax >= 0) {
								if (similarityTable[i][j] > similarityTable[iMax][jMax]) {
									iMax = i;
									jMax = j;
								}
							} else {
								iMax = i;
								jMax = j;
							}
						}
					}
				}

				if (iMax >= 0 && jMax >= 0) {
					// merge if both not in
					if (similarityTable[iMax][jMax] >= threshold) {
						modified = !mergeable.contains(iMax) || !mergeable.contains(jMax);
						mergeable.add(iMax);
						mergeable.add(jMax);
					} else {
						modified = false;
					}
				}
			}

			// convert set to branch
			for (int i : mergeable) {
				// get child
				TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> child = children.get(i);

				// it is an implicit branch which will be converted in a postprocessing step
				recursiveExtractPaths(child,
						currentTreeNode.addChildren(new DescriptorTransition<IAbstractUsageDescriptor>(
								child.getData().getCall(), child.getData().getProbability())),
						currentTree, container, threshold);
			}

			// for all others recursive with a new tree
			for (int i = 0; i < children.size(); i++) {
				// get child
				TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> child = children.get(i);
				if (!mergeable.contains(i)) {
					Pair<Tree<DescriptorTransition<IAbstractUsageDescriptor>>, Map<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>, TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>>> copy = copyTree(
							currentTree);
					recursiveExtractPaths(child, copy.getRight().get(currentTreeNode).addChildren(child.getData()),
							copy.getLeft(), container, threshold);
				}
			}
		} else {
			// easily add a child and start recursion
			if (currentNode.getChildren().size() == 0) {
				// we are finished here => add a child
				container.add(currentTree);
			} else {
				// => size == 1
				TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> oldNode = currentNode.getChildren().get(0);

				// add node
				TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> nextNode = currentTreeNode
						.addChildren(oldNode.getData());

				// update probability (this can differ when we merge paths)
				nextNode.getData().setProbability(currentTreeNode.getData().getProbability());

				// start recursion
				recursiveExtractPaths(oldNode, nextNode, currentTree, container, threshold);
			}
		}

	}

	private double calculateSimilarity(TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> a,
			TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> b) {
		Map<String, Integer> callsA = new HashMap<>();
		Map<String, Integer> callsB = new HashMap<>();

		recursiveEstimateCalls(a, callsA);
		recursiveEstimateCalls(b, callsB);

		// cross insertion for missing calls
		for (String keyA : callsA.keySet()) {
			if (!callsB.containsKey(keyA)) {
				callsB.put(keyA, 0);
			}
		}

		for (String keyB : callsB.keySet()) {
			if (!callsA.containsKey(keyB)) {
				callsA.put(keyB, 0);
			}
		}

		// transform to vectors
		int[] vectorA = new int[callsA.size()];
		int[] vectorB = new int[callsB.size()];
		int currentId = 0;
		Map<String, Integer> keyMapping = new HashMap<>();
		for (Entry<String, Integer> entry : callsA.entrySet()) {
			if (!keyMapping.containsKey(entry.getKey())) {
				keyMapping.put(entry.getKey(), currentId++);
			}
			int index = keyMapping.get(entry.getKey());
			vectorA[index] = entry.getValue();
			vectorB[index] = callsB.get(entry.getKey());
		}

		// get distance
		double sum = 0;
		for (int i = 0; i < vectorA.length; i++) {
			sum += Math.pow(vectorA[i] - vectorB[i], 2);
		}
		double distance = Math.sqrt(sum);

		// normalize it
		double maxDistanceA = Math.sqrt(Arrays.stream(vectorA).mapToDouble(i -> Math.pow(i, 2d)).sum());
		double maxDistanceB = Math.sqrt(Arrays.stream(vectorB).mapToDouble(i -> Math.pow(i, 2d)).sum());

		return 1.0d - distance / Math.max(maxDistanceA, maxDistanceB);
	}

	private void recursiveEstimateCalls(TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> node,
			Map<String, Integer> calls) {
		IAbstractUsageDescriptor descriptor = node.getData().getCall();
		if (descriptor != null && descriptor instanceof UsageServiceCallDescriptor) {
			String serviceId = ((UsageServiceCallDescriptor) descriptor).getServiceId();
			if (calls.containsKey(serviceId)) {
				calls.put(serviceId, calls.get(serviceId) + 1);
			} else {
				calls.put(serviceId, 1);
			}
		}

		// start recursion
		for (TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> child : node.getChildren()) {
			recursiveEstimateCalls(child, calls);
		}
	}

	private Pair<Tree<DescriptorTransition<IAbstractUsageDescriptor>>, Map<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>, TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>>> copyTree(
			Tree<DescriptorTransition<IAbstractUsageDescriptor>> from) {
		Map<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>, TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>> oldMapping = new HashMap<>();
		Tree<DescriptorTransition<IAbstractUsageDescriptor>> to = new Tree<>(from.getRoot().getData());
		oldMapping.put(from.getRoot(), to.getRoot());
		copyTreeRecursive(from.getRoot(), to.getRoot(), oldMapping);
		return Pair.of(to, oldMapping);
	}

	private void copyTreeRecursive(TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> from,
			TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> to,
			Map<TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>, TreeNode<DescriptorTransition<IAbstractUsageDescriptor>>> mapping) {
		for (TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> child : from.getChildren()) {
			TreeNode<DescriptorTransition<IAbstractUsageDescriptor>> copy = to.addChildren(
					new DescriptorTransition<>(child.getData().getCall(), child.getData().getProbability()));
			mapping.put(child, copy);
			copyTreeRecursive(copy, child, mapping);
		}
	}

}
