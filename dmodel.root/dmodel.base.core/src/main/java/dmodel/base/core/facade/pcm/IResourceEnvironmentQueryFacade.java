package dmodel.base.core.facade.pcm;

import java.util.List;

import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import dmodel.base.core.facade.IResettableQueryFacade;

public interface IResourceEnvironmentQueryFacade extends IResettableQueryFacade {

	List<ResourceContainer> getResourceContainers();

	void removeContainer(ResourceContainer depContainer);

}
