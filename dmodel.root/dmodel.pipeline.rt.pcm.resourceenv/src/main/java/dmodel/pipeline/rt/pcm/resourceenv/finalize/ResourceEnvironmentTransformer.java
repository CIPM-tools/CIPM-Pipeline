package dmodel.pipeline.rt.pcm.resourceenv.finalize;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import dmodel.pipeline.core.facade.IPCMQueryFacade;
import dmodel.pipeline.core.facade.IRuntimeEnvironmentQueryFacade;
import dmodel.pipeline.rt.pcm.resourceenv.data.EnvironmentData;
import dmodel.pipeline.rt.pcm.resourceenv.data.Host;
import dmodel.pipeline.rt.pcm.resourceenv.data.HostLink;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.pipeline.shared.pcm.util.deprecation.SimpleDeprecationProcessor;
import dmodel.pipeline.vsum.facade.ISpecificVsumFacade;
import lombok.extern.java.Log;

@Log
public class ResourceEnvironmentTransformer implements IResourceEnvironmentDeduction {
	private SimpleDeprecationProcessor deprecationProcessor;

	public ResourceEnvironmentTransformer() {
		this.deprecationProcessor = new SimpleDeprecationProcessor(3);
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
			pcm.getResourceEnvironment().removeContainer(depContainer);
		}
		if (toRemove.size() > 0) {
			log.info("Removed " + toRemove.size() + " containers from the resource environment model.");
		}
	}

}