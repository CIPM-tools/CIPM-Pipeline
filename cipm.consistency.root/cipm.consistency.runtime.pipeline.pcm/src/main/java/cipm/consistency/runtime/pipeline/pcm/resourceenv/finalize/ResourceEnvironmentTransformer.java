package cipm.consistency.runtime.pipeline.pcm.resourceenv.finalize;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cipm.consistency.base.core.facade.IPCMQueryFacade;
import cipm.consistency.base.core.facade.IRuntimeEnvironmentQueryFacade;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import cipm.consistency.base.shared.pcm.util.deprecation.SimpleDeprecationProcessor;
import cipm.consistency.base.vsum.facade.ISpecificVsumFacade;
import cipm.consistency.runtime.pipeline.pcm.resourceenv.data.EnvironmentData;
import cipm.consistency.runtime.pipeline.pcm.resourceenv.data.Host;
import cipm.consistency.runtime.pipeline.pcm.resourceenv.data.HostLink;
import lombok.extern.java.Log;

@Log
public class ResourceEnvironmentTransformer implements IResourceEnvironmentDeduction {
	private SimpleDeprecationProcessor deprecationProcessor;

	public ResourceEnvironmentTransformer() {
		this.deprecationProcessor = new SimpleDeprecationProcessor(1);
	}

	@Override
	public void processEnvironmentData(IPCMQueryFacade pcm, IRuntimeEnvironmentQueryFacade rem,
			ISpecificVsumFacade mapping, EnvironmentData data) {
		processHosts(pcm, rem, mapping, data.getHosts());
		processLinks(pcm, rem, mapping, data.getConnections());

		deprecationProcessor.iterationFinished();
	}

	private void processLinks(IPCMQueryFacade pcm, IRuntimeEnvironmentQueryFacade rem, ISpecificVsumFacade mapping,
			List<HostLink> links) {
		for (HostLink link : links) {
			if (!rem.containsLink(link.getFromId(), link.getToId())) {
				rem.createResourceContainerLink(link.getFromId(), link.getToId());
			}
		}
	}

	private void processHosts(IPCMQueryFacade pcm, IRuntimeEnvironmentQueryFacade rem, ISpecificVsumFacade mapping,
			List<Host> hosts) {
		// create new ones
		for (Host host : hosts) {
			if (!rem.containsHostId(host.getId())) {
				rem.createResourceContainer(host.getId(), host.getName());
			}
		}

		// remove all that are unused
		removeUnusedContainers(pcm, rem, mapping, hosts);
	}

	private void removeUnusedContainers(IPCMQueryFacade pcm, IRuntimeEnvironmentQueryFacade env,
			ISpecificVsumFacade vsumFacade, List<Host> hosts) {
		// collect used containers
		Set<String> presentContainerIds = Sets.newHashSet();
		for (Host host : hosts) {
			if (env.containsHostId(host.getId())) {
				RuntimeResourceContainer rec = env.getContainerById(host.getId());
				if (rec != null) {
					Optional<ResourceContainer> correspondingResourceContainer = vsumFacade
							.getCorrespondingResourceContainer(rec);
					if (correspondingResourceContainer.isPresent()) {
						presentContainerIds.add(correspondingResourceContainer.get().getId());
					}
				}
			}
		}

		// remove unused based on used
		this.removeUnusedContainers(pcm, vsumFacade, presentContainerIds);
	}

	private void removeUnusedContainers(IPCMQueryFacade pcm, ISpecificVsumFacade vsumFacade,
			Set<String> presentContainerIds) {
		// resolve containers to delete
		List<ResourceContainer> toRemove = Lists.newArrayList();
		for (ResourceContainer presentContainer : pcm.getResourceEnvironment().getResourceContainers()) {
			if (!presentContainerIds.contains(presentContainer.getId())) {
				// check allocations
				if (!pcm.getAllocation().hasDeployments(presentContainer)) {
					toRemove.add(presentContainer);
				}
			}
		}

		// remove containers
		for (ResourceContainer depContainer : toRemove) {
			if (this.deprecationProcessor.shouldDelete(depContainer)) {
				pcm.getResourceEnvironment().removeContainer(depContainer);
				removeCorrespondingLinks(pcm, depContainer);
			}
		}
		if (toRemove.size() > 0) {
			log.info("Removed " + toRemove.size() + " containers from the resource environment model.");
		}
	}

	private void removeCorrespondingLinks(IPCMQueryFacade pcm, ResourceContainer depContainer) {
		List<LinkingResource> toRemoveLinks = Lists.newArrayList();
		for (LinkingResource link : pcm.getResourceEnvironment().getLinkingResources()) {
			if (link.getConnectedResourceContainers_LinkingResource().stream()
					.anyMatch(cont -> cont.getId().equals(depContainer.getId()))) {
				toRemoveLinks.add(link);
			}
		}

		for (LinkingResource link : toRemoveLinks) {
			pcm.getResourceEnvironment().removeLink(link);
		}

		log.info("Removed " + toRemoveLinks.size() + " links from the resource environment model.");
	}

}