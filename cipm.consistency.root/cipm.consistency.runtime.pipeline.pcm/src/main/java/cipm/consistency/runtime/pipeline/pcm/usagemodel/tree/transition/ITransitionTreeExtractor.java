package cipm.consistency.runtime.pipeline.pcm.usagemodel.tree.transition;

import java.util.List;

import cipm.consistency.base.core.facade.pcm.IRepositoryQueryFacade;
import cipm.consistency.base.core.facade.pcm.ISystemQueryFacade;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.ServiceCallSession;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.data.UsageServiceCallDescriptor;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.tree.DescriptorTransition;

public interface ITransitionTreeExtractor {

	public Tree<DescriptorTransition<UsageServiceCallDescriptor>> extractProbabilityCallTree(
			List<ServiceCallSession> sessions, IRepositoryQueryFacade repository, ISystemQueryFacade system);

}
