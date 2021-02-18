package cipm.consistency.runtime.pipeline.blackboard.facade.impl;

import java.util.List;

import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.core.facade.pcm.IResourceEnvironmentQueryFacade;
import cipm.consistency.base.vsum.facade.ISpecificVsumFacade;
import cipm.consistency.base.vsum.manager.VsumManager;
import cipm.consistency.base.vsum.manager.VsumManager.VsumChangeSource;
import lombok.Setter;

@Component
public class ResourceEnvironmentQueryFacadeImpl implements IResourceEnvironmentQueryFacade {

	@Autowired
	@Setter
	private IPcmModelProvider pcmModelProvider;

	@Autowired
	private ISpecificVsumFacade vsum;

	@Autowired
	private VsumManager vsumManager;

	@Override
	public List<ResourceContainer> getResourceContainers() {
		return pcmModelProvider.getResourceEnvironment().getResourceContainer_ResourceEnvironment();
	}

	@Override
	public List<LinkingResource> getLinkingResources() {
		return pcmModelProvider.getResourceEnvironment().getLinkingResources__ResourceEnvironment();
	}

	@Override
	public void removeContainer(ResourceContainer depContainer) {
		vsumManager.executeTransaction(() -> {
			pcmModelProvider.getResourceEnvironment().getResourceContainer_ResourceEnvironment().remove(depContainer);
			vsum.deletedObject(depContainer, VsumChangeSource.RESURCE_ENVIRONMENT);
			return null;
		});
	}

	@Override
	public void removeLink(LinkingResource link) {
		vsumManager.executeTransaction(() -> {
			pcmModelProvider.getResourceEnvironment().getLinkingResources__ResourceEnvironment().remove(link);
			vsum.deletedObject(link, VsumChangeSource.RESURCE_ENVIRONMENT);
			return null;
		});
	}

	@Override
	public void reset(boolean hard) {
		// nothing to do here
	}

}
