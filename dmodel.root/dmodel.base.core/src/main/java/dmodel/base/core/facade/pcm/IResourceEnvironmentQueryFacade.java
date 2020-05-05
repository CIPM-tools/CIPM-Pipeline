package dmodel.base.core.facade.pcm;

import java.util.List;

import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import dmodel.base.core.facade.IResettableQueryFacade;

/**
 * Facade for accessing and modifying the underlying repository model.
 * 
 * @author David Monschein
 *
 */
public interface IResourceEnvironmentQueryFacade extends IResettableQueryFacade {
	/**
	 * Gets all resource containers in the resource environment.
	 * 
	 * @return list of all resource containers in the environment
	 */
	List<ResourceContainer> getResourceContainers();

	/**
	 * Removes a container from the resource environment
	 * 
	 * @param depContainer the container that should be removed
	 */
	void removeContainer(ResourceContainer depContainer);

}
