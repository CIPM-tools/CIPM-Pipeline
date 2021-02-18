package cipm.consistency.runtime.pipeline.pcm.usagemodel.tree.path;

import java.util.List;

import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.IAbstractUsageDescriptor;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.tree.DescriptorTransition;

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
