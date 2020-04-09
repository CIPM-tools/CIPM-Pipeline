package dmodel.pipeline.core.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.core.facade.IPCMQueryFacade;
import dmodel.pipeline.core.facade.pcm.IAllocationQueryFacade;
import dmodel.pipeline.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.pipeline.core.facade.pcm.ISystemQueryFacade;
import dmodel.pipeline.core.facade.pcm.IUsageQueryFacade;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

@Component
public class PcmQueryImpl implements IPCMQueryFacade {
	@Autowired
	private IRepositoryQueryFacade repositoryQueryFacade;

	@Autowired
	private ISystemQueryFacade systemQueryFacade;

	@Autowired
	private IAllocationQueryFacade allocationQueryFacade;

	@Autowired
	private IUsageQueryFacade usageQueryFacade;

	@Autowired
	private IPcmModelProvider pcmProvider;

	@Override
	public IRepositoryQueryFacade getRepository() {
		return repositoryQueryFacade;
	}

	@Override
	public ISystemQueryFacade getSystem() {
		return systemQueryFacade;
	}

	@Override
	public IAllocationQueryFacade getAllocation() {
		return allocationQueryFacade;
	}

	@Override
	public IUsageQueryFacade getUsage() {
		return usageQueryFacade;
	}

	@Override
	public InMemoryPCM getRaw() {
		return pcmProvider.getRaw();
	}

	@Override
	public InMemoryPCM getDeepCopy() {
		return pcmProvider.getRaw().copyDeep();
	}

}
