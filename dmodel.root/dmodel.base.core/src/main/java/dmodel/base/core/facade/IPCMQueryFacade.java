package dmodel.base.core.facade;

import dmodel.base.core.facade.pcm.IAllocationQueryFacade;
import dmodel.base.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.base.core.facade.pcm.IResourceEnvironmentQueryFacade;
import dmodel.base.core.facade.pcm.ISystemQueryFacade;
import dmodel.base.core.facade.pcm.IUsageQueryFacade;
import dmodel.base.shared.pcm.InMemoryPCM;

/**
 * Main facade for accessing the Palladio Component Model (PCM) parts. The
 * access to the raw underlying models should only be used if absolutely
 * necessary! The sub-facades provide caching functionality and often make it
 * much easier to modify and access the models.
 * 
 * @author David Monschein
 *
 */
public interface IPCMQueryFacade {

	/**
	 * Gets the sub-facade that can be used to access/modify the underlying
	 * repository model.
	 * 
	 * @return sub-facade that can be used to access/modify the underlying
	 *         repository model
	 */
	public IRepositoryQueryFacade getRepository();

	/**
	 * Gets the sub-facade that can be used to access/modify the underlying system
	 * model.
	 * 
	 * @return sub-facade that can be used to access/modify the underlying system
	 *         model
	 */
	public ISystemQueryFacade getSystem();

	/**
	 * Gets the sub-facade that can be used to access/modify the underlying
	 * allocation model.
	 * 
	 * @return sub-facade that can be used to access/modify the underlying
	 *         allocation model
	 */
	public IAllocationQueryFacade getAllocation();

	/**
	 * Gets the sub-facade that can be used to access/modify the underlying usage
	 * model.
	 * 
	 * @return sub-facade that can be used to access/modify the underlying usage
	 *         model
	 */
	public IUsageQueryFacade getUsage();

	/**
	 * Gets the sub-facade that can be used to access/modify the underlying resource
	 * environment model.
	 * 
	 * @return sub-facade that can be used to access/modify the underlying resource
	 *         environment model
	 */
	public IResourceEnvironmentQueryFacade getResourceEnvironment();

	/**
	 * Gets the raw underlying model with direct access to all model parts of the
	 * PCM. Use this with caution as it may interfere with the caching
	 * functionalities of the facades.
	 * 
	 * @return the raw underlying PCM
	 */
	public InMemoryPCM getRaw();

	/**
	 * Creates a deep clone of all underlying PCM parts. The references between the
	 * model elements are modified so the resulting model is a full clone of the
	 * current PCM with no references to the original PCM parts.
	 * 
	 * @return deep clone of the underlying PCM
	 */
	public InMemoryPCM getDeepCopy();

}
