package dmodel.pipeline.rt.pcm.usagemodel.tree.path;

import java.util.List;

import dmodel.pipeline.rt.pcm.usagemodel.data.IAbstractUsageDescriptor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.DescriptorTransition;
import dmodel.pipeline.shared.structure.Tree;

public interface IPathExtractor {

	/**
	 * 
	 * @param tree
	 * @param subSimilarityThres paths should be split up in two if the subtree
	 *                           similarity is lower than this value
	 * @return
	 */
	List<Tree<DescriptorTransition<IAbstractUsageDescriptor>>> extractRelevantPaths(
			Tree<DescriptorTransition<IAbstractUsageDescriptor>> tree, float subSimilarityThres);

}
