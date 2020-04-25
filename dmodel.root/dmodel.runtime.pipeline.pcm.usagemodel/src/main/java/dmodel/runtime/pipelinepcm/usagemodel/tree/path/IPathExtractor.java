package dmodel.runtime.pipelinepcm.usagemodel.tree.path;

import java.util.List;

import dmodel.base.shared.structure.Tree;
import dmodel.runtime.pipelinepcm.usagemodel.data.IAbstractUsageDescriptor;
import dmodel.runtime.pipelinepcm.usagemodel.tree.DescriptorTransition;

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
