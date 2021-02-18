package cipm.consistency.base.core.facade.pcm;

import java.util.List;

import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import cipm.consistency.base.core.facade.IResettableQueryFacade;

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
	 * Gets all links in the resource environment.
	 * 
	 * @return list of all links between the containers within the resource
	 *         environment
	 */
	List<LinkingResource> getLinkingResources();

	/**
	 * Removes a container from the resource environment.
	 * 
	 * @param depContainer the container that should be removed
	 */
	void removeContainer(ResourceContainer depContainer);

	/**
	 * Removes a link from the resource environment.
	 * 
	 * @param link linking resource that should be removed
	 */
	void removeLink(LinkingResource link);

}
