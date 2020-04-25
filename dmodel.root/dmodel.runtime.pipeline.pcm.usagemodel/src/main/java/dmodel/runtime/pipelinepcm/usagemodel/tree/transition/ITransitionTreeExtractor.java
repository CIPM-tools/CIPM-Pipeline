package dmodel.runtime.pipelinepcm.usagemodel.tree.transition;

import java.util.List;

import dmodel.base.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.base.core.facade.pcm.ISystemQueryFacade;
import dmodel.base.shared.structure.Tree;
import dmodel.runtime.pipelinepcm.usagemodel.ServiceCallSession;
import dmodel.runtime.pipelinepcm.usagemodel.data.UsageServiceCallDescriptor;
import dmodel.runtime.pipelinepcm.usagemodel.tree.DescriptorTransition;

public interface ITransitionTreeExtractor {

	public Tree<DescriptorTransition<UsageServiceCallDescriptor>> extractProbabilityCallTree(
			List<ServiceCallSession> sessions, IRepositoryQueryFacade repository, ISystemQueryFacade system);

}
