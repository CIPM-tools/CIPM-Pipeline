package dmodel.pipeline.rt.pcm.resourceenv.finalize;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import com.google.common.collect.Lists;

import dmodel.pipeline.core.facade.IRuntimeEnvironmentQueryFacade;
import dmodel.pipeline.rt.pcm.resourceenv.data.EnvironmentData;
import dmodel.pipeline.rt.pcm.resourceenv.data.Host;
import dmodel.pipeline.rt.pcm.resourceenv.data.HostLink;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
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
	public void processEnvironmentData(IRuntimeEnvironmentQueryFacade rem, ISpecificVsumFacade mapping,
			EnvironmentData data) {
		processHosts(rem, mapping, data.getHosts());
		processLinks(rem, mapping, data.getConnections());

		deprecationProcessor.iterationFinished();
	}

	private void processLinks(IRuntimeEnvironmentQueryFacade rem, ISpecificVsumFacade mapping, List<HostLink> links) {
		for (HostLink link : links) {
			if (!rem.containsLink(link.getFromId(), link.getToId())) {
				rem.createResourceContainerLink(link.getFromId(), link.getToId());
			}
		}
	}

	private void processHosts(IRuntimeEnvironmentQueryFacade rem, ISpecificVsumFacade mapping, List<Host> hosts) {
		// create new ones
		for (Host host : hosts) {
			if (!rem.containsHostId(host.getId())) {
				rem.createResourceContainer(host.getId(), host.getName());
			}
		}

		// remove all that are unused
		// TODO review removal -> put it into the reactions?
		// removeUnusedContainers(pcm, mapping, usedContainerIds);
	}

	private void removeUnusedContainers(InMemoryPCM pcm, ISpecificVsumFacade vsumFacade, Set<String> usedContainerIds) {
		List<ResourceContainer> toRemove = pcm.getResourceEnvironmentModel().getResourceContainer_ResourceEnvironment()
				.stream().filter(r -> !usedContainerIds.contains(r.getId())).collect(Collectors.toList());
		toRemove.forEach(tr -> {
			if (deprecationProcessor.shouldDelete(tr)) {
				pcm.getResourceEnvironmentModel().getResourceContainer_ResourceEnvironment().remove(tr);
				vsumFacade.deletedObject(tr);

				List<LinkingResource> toRemoveLinkingResources = Lists.newArrayList();
				pcm.getResourceEnvironmentModel().getLinkingResources__ResourceEnvironment().forEach(lr -> {
					if (lr.getConnectedResourceContainers_LinkingResource().contains(tr)) {
						lr.getConnectedResourceContainers_LinkingResource().remove(tr);
						if (lr.getConnectedResourceContainers_LinkingResource().size() <= 1) {
							toRemoveLinkingResources.add(lr);
						}
					}
				});

				toRemoveLinkingResources.forEach(tlr -> {
					pcm.getResourceEnvironmentModel().getLinkingResources__ResourceEnvironment().remove(tlr);
					vsumFacade.deletedObject(tlr);
				});
			}
		});
	}

}