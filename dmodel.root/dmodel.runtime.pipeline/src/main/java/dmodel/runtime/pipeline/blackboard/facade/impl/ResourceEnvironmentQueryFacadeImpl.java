package dmodel.runtime.pipeline.blackboard.facade.impl;

import java.util.List;

import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.base.core.IPcmModelProvider;
import dmodel.base.core.facade.pcm.IResourceEnvironmentQueryFacade;
import dmodel.base.vsum.facade.ISpecificVsumFacade;
import dmodel.base.vsum.manager.VsumManager;
import dmodel.base.vsum.manager.VsumManager.VsumChangeSource;

@Component
public class ResourceEnvironmentQueryFacadeImpl implements IResourceEnvironmentQueryFacade {

	@Autowired
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
	public void removeContainer(ResourceContainer depContainer) {
		vsumManager.executeTransaction(() -> {
			pcmModelProvider.getResourceEnvironment().getResourceContainer_ResourceEnvironment().remove(depContainer);
			vsum.deletedObject(depContainer, VsumChangeSource.RESURCE_ENVIRONMENT);
			return null;
		});
	}

	@Override
	public void reset(boolean hard) {
		// nothing to do here
	}

}
