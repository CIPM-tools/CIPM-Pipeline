package cipm.consistency.base.core.mocks;

import java.util.List;

import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.springframework.beans.factory.annotation.Autowired;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.core.facade.pcm.IResourceEnvironmentQueryFacade;

public class ResourceEnvironmentFacadeMockImpl implements IResourceEnvironmentQueryFacade {

	@Autowired
	private IPcmModelProvider pcmModelProvider;

	@Override
	public List<ResourceContainer> getResourceContainers() {
		return pcmModelProvider.getResourceEnvironment().getResourceContainer_ResourceEnvironment();
	}

	@Override
	public void removeContainer(ResourceContainer depContainer) {
		// not available
	}

	@Override
	public void reset(boolean hard) {
	}

	@Override
	public List<LinkingResource> getLinkingResources() {
		return pcmModelProvider.getResourceEnvironment().getLinkingResources__ResourceEnvironment();
	}

	@Override
	public void removeLink(LinkingResource link) {
		// not available
	}

}
