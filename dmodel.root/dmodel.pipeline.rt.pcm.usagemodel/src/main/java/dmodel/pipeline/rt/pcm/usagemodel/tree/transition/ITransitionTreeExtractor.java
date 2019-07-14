package dmodel.pipeline.rt.pcm.usagemodel.tree.transition;

import java.util.List;

import dmodel.pipeline.dt.mmmodel.UsageServiceCallDescriptor;
import dmodel.pipeline.rt.pcm.usagemodel.ServiceCallSession;
import dmodel.pipeline.rt.pcm.usagemodel.tree.DescriptorTransition;
import dmodel.pipeline.shared.structure.Tree;

public interface ITransitionTreeExtractor {

	public Tree<DescriptorTransition<UsageServiceCallDescriptor>> extractProbabilityCallTree(
			List<ServiceCallSession> sessions);

}
