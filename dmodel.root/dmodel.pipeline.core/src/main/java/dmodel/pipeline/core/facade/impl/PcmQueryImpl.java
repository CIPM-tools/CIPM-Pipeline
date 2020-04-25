package dmodel.pipeline.core.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.core.facade.IPCMQueryFacade;
import dmodel.pipeline.core.facade.pcm.IAllocationQueryFacade;
import dmodel.pipeline.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.pipeline.core.facade.pcm.IResourceEnvironmentQueryFacade;
import dmodel.pipeline.core.facade.pcm.ISystemQueryFacade;
import dmodel.pipeline.core.facade.pcm.IUsageQueryFacade;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

/**
 * Implementation according to {@link IPCMQueryFacade}. This implementation can
 * be used to get the sub-facades which are auto-wired by spring.
 * 
 * @author David Monschein
 *
 */
@Component
public class PcmQueryImpl implements IPCMQueryFacade {
	/**
	 * Facade for accessing/modifying the repository model.
	 */
	@Autowired
	private IRepositoryQueryFacade repositoryQueryFacade;

	/**
	 * Facade for accessing/modifying the system model.
	 */
	@Autowired
	private ISystemQueryFacade systemQueryFacade;

	/**
	 * Facade for accessing/modifying the allocation model.
	 */
	@Autowired
	private IAllocationQueryFacade allocationQueryFacade;

	/**
	 * Facade for accessing/modifying the usage model.
	 */
	@Autowired
	private IUsageQueryFacade usageQueryFacade;

	/**
	 * Facade for accessing/modifying the resource environment model.
	 */
	@Autowired
	private IResourceEnvironmentQueryFacade resourceEnvironmentQueryFacade;

	/**
	 * Used to access the raw underlying models to be able to create deep copies.
	 */
	@Autowired
	private IPcmModelProvider pcmProvider;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IResourceEnvironmentQueryFacade getResourceEnvironment() {
		return resourceEnvironmentQueryFacade;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IRepositoryQueryFacade getRepository() {
		return repositoryQueryFacade;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ISystemQueryFacade getSystem() {
		return systemQueryFacade;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IAllocationQueryFacade getAllocation() {
		return allocationQueryFacade;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUsageQueryFacade getUsage() {
		return usageQueryFacade;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InMemoryPCM getRaw() {
		return pcmProvider.getRaw();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InMemoryPCM getDeepCopy() {
		return pcmProvider.getRaw().copyDeep();
	}

}
