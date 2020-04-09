package dmodel.pipeline.core.facade;

import dmodel.pipeline.core.facade.pcm.IAllocationQueryFacade;
import dmodel.pipeline.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.pipeline.core.facade.pcm.ISystemQueryFacade;
import dmodel.pipeline.core.facade.pcm.IUsageQueryFacade;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

public interface IPCMQueryFacade {

	public IRepositoryQueryFacade getRepository();

	public ISystemQueryFacade getSystem();

	public IAllocationQueryFacade getAllocation();

	public IUsageQueryFacade getUsage();

	public InMemoryPCM getRaw();

	public InMemoryPCM getDeepCopy();

}
