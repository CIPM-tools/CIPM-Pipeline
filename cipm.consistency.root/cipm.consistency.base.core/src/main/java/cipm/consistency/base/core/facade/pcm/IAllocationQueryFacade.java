package cipm.consistency.base.core.facade.pcm;

import java.util.List;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import cipm.consistency.base.core.facade.IResettableQueryFacade;

/**
 * Facade for accessing an underlying allocation model (see {@link Allocation}).
 * It supports reading and writing operations.
 * 
 * @author David Monschein
 *
 */
public interface IAllocationQueryFacade extends IResettableQueryFacade {

	/**
	 * Deploys an assembly context on a given container by creating an
	 * {@link AllocationContext} within the underlying allocation model.
	 * 
	 * @param ret  the assembly context that should be deployed
	 * @param host the resource container on which the assembly context should be
	 *             deployed
	 */
	void deployAssembly(AssemblyContext ret, ResourceContainer host);

	/**
	 * Removes the corresponding deployment of an assembly.
	 * 
	 * @param ctx the assembly context
	 */
	void undeployAssembly(AssemblyContext ctx);

	/**
	 * Determines whether a given assembly context has already been deployed.
	 * 
	 * @param ac the assembly context
	 * @return true if the assembly context is deployed, false if not
	 */
	boolean isDeployed(AssemblyContext ac);

	/**
	 * Gets the container on which a certain assembly context is deployed.
	 * 
	 * @param asCtx the assembly context for which the corresponding container
	 *              should be determined
	 * @return the container on which a certain assembly context is deployed or null
	 *         if it is not deployed
	 */
	ResourceContainer getContainerByAssembly(AssemblyContext asCtx);

	/**
	 * Gets the assembly contexts with a specific type (component) that are deployed
	 * on a given container.
	 * 
	 * @param componentType the component type of the assembly contexts
	 * @param container     the container
	 * @return all assembly contexts conforming to the given component and deployed
	 *         on the given container
	 */
	List<AssemblyContext> getDeployedAssembly(BasicComponent componentType, ResourceContainer container);

	/**
	 * Determines whether there is any assembly context deployed on a given
	 * container.
	 * 
	 * @param presentContainer the resource container
	 * @return true if there is an assembly context deployed on that container,
	 *         false if not
	 */
	boolean hasDeployments(ResourceContainer presentContainer);

}
