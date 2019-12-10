package dmodel.pipeline.rt.pcm.resourceenv.finalize;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.palladiosimulator.pcm.resourceenvironment.CommunicationLinkResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.resourcetype.ResourceRepository;
import org.palladiosimulator.pcm.resourcetype.SchedulingPolicy;

import dmodel.pipeline.models.mapping.HostIDMapping;
import dmodel.pipeline.models.mapping.MappingFactory;
import dmodel.pipeline.models.mapping.PalladioRuntimeMapping;
import dmodel.pipeline.rt.pcm.resourceenv.data.EnvironmentData;
import dmodel.pipeline.rt.pcm.resourceenv.data.Host;
import dmodel.pipeline.rt.pcm.resourceenv.data.HostLink;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
import lombok.extern.java.Log;

@Log
public class ResourceEnvironmentTransformer implements IResourceEnvironmentDeduction {
	private static final ResourceRepository DEFAULT_RESOURCE_REPO = PCMUtils.getDefaultResourceRepository();
	private static final ProcessingResourceType CPU_PROC_TYPE = PCMUtils.getElementById(DEFAULT_RESOURCE_REPO,
			ProcessingResourceType.class, "_oro4gG3fEdy4YaaT-RYrLQ");
	private static final SchedulingPolicy CPU_SHARING_POLICY = PCMUtils.getElementById(DEFAULT_RESOURCE_REPO,
			SchedulingPolicy.class, "ProcessorSharing");

	@Override
	public void processEnvironmentData(InMemoryPCM pcm, PalladioRuntimeMapping mapping, EnvironmentData data) {
		processHosts(pcm, mapping, data.getHosts());
		processLinks(pcm, mapping, data.getConnections());
	}

	private void processLinks(InMemoryPCM pcm, PalladioRuntimeMapping mapping, List<HostLink> links) {
		links.stream().forEach(link -> {
			Optional<HostIDMapping> hostFrom = mapping.getHostMappings().stream()
					.filter(mp -> mp.getHostID().equals(link.getFromId())).findFirst();
			Optional<HostIDMapping> hostTo = mapping.getHostMappings().stream()
					.filter(mp -> mp.getHostID().equals(link.getToId())).findFirst();

			if (hostFrom.isPresent() && hostTo.isPresent()) {
				Optional<LinkingResource> belLink = getLinkingResource(pcm.getResourceEnvironmentModel(),
						hostFrom.get().getPcmContainerID(), hostTo.get().getPcmContainerID());
				if (!belLink.isPresent()) {
					// create link
					createLink(pcm.getResourceEnvironmentModel(), hostFrom.get().getPcmContainerID(),
							hostTo.get().getPcmContainerID());
				}
			} else {
				log.warning("Updating the Resource Environment failed due to mapping problems.");
			}
		});
	}

	private void processHosts(InMemoryPCM pcm, PalladioRuntimeMapping mapping, List<Host> hosts) {
		hosts.stream().forEach(host -> {
			Optional<HostIDMapping> refMapping = mapping.getHostMappings().stream()
					.filter(mp -> mp.getHostID().equals(host.getId())).findFirst();

			if (refMapping.isPresent()) {
				ResourceContainer refContainer = PCMUtils.getElementById(pcm.getResourceEnvironmentModel(),
						ResourceContainer.class, refMapping.get().getPcmContainerID());

				if (refContainer == null) {
					// create it
					HostIDMapping resMapping = createContainer(pcm.getResourceEnvironmentModel(), host.getId(),
							host.getName());
					mapping.getHostMappings().add(resMapping);
				}
			} else {
				Optional<ResourceContainer> optContainer = getContainerByName(pcm.getResourceEnvironmentModel(),
						host.getName());
				if (!optContainer.isPresent()) {
					HostIDMapping resMapping = createContainer(pcm.getResourceEnvironmentModel(), host.getId(),
							host.getName());
					mapping.getHostMappings().add(resMapping);
				} else {
					HostIDMapping nMapping = MappingFactory.eINSTANCE.createHostIDMapping();
					nMapping.setHostID(host.getId());
					nMapping.setPcmContainerID(optContainer.get().getId());

					mapping.getHostMappings().add(nMapping);
				}
			}
		});
	}

	private Optional<ResourceContainer> getContainerByName(ResourceEnvironment parent, String name) {
		return PCMUtils.getElementsByType(parent, ResourceContainer.class).stream()
				.filter(c -> c.getEntityName().equals(name)).findFirst();
	}

	private HostIDMapping createContainer(ResourceEnvironment resourceEnvironmentModel, String hostId,
			String hostName) {
		ResourceContainer container = ResourceenvironmentFactory.eINSTANCE.createResourceContainer();
		container.setEntityName(hostName);

		ProcessingResourceSpecification cpuSpec = createDefaultCPUSpecficiation();
		container.getActiveResourceSpecifications_ResourceContainer().add(cpuSpec);
		cpuSpec.setResourceContainer_ProcessingResourceSpecification(container);

		// create mapping
		HostIDMapping nMapping = MappingFactory.eINSTANCE.createHostIDMapping();
		nMapping.setHostID(hostId);
		nMapping.setPcmContainerID(container.getId());

		// add the container
		resourceEnvironmentModel.getResourceContainer_ResourceEnvironment().add(container);
		log.info("Created new resource environment container (name: '" + hostName + "').");

		return nMapping;
	}

	private ProcessingResourceSpecification createDefaultCPUSpecficiation() {
		ProcessingResourceSpecification spec = ResourceenvironmentFactory.eINSTANCE
				.createProcessingResourceSpecification();
		spec.setMTTF(0);
		spec.setMTTR(0);
		spec.setNumberOfReplicas(0);
		spec.setProcessingRate_ProcessingResourceSpecification(PCMUtils.createRandomVariableFromString("1"));
		spec.setActiveResourceType_ActiveResourceSpecification(CPU_PROC_TYPE);
		spec.setSchedulingPolicy(CPU_SHARING_POLICY);

		// backlink
		spec.getProcessingRate_ProcessingResourceSpecification()
				.setProcessingResourceSpecification_processingRate_PCMRandomVariable(spec);
		return spec;
	}

	private Optional<LinkingResource> getLinkingResource(ResourceEnvironment env, String... ids) {
		return PCMUtils.getElementsByType(env, LinkingResource.class).stream().filter(lr -> {
			return ids.length == lr.getConnectedResourceContainers_LinkingResource().size()
					&& lr.getConnectedResourceContainers_LinkingResource().stream()
							.allMatch(r -> Arrays.stream(ids).anyMatch(r.getId()::equals));
		}).findFirst();
	}

	private void createLink(ResourceEnvironment resourceEnvironmentModel, String pcmContainerID,
			String pcmContainerID2) {
		ResourceContainer container1 = PCMUtils.getElementById(resourceEnvironmentModel, ResourceContainer.class,
				pcmContainerID);
		ResourceContainer container2 = PCMUtils.getElementById(resourceEnvironmentModel, ResourceContainer.class,
				pcmContainerID2);

		LinkingResource link = ResourceenvironmentFactory.eINSTANCE.createLinkingResource();
		link.setCommunicationLinkResourceSpecifications_LinkingResource(createDefaultLinkSpec());
		link.getConnectedResourceContainers_LinkingResource().add(container1);
		link.getConnectedResourceContainers_LinkingResource().add(container2);

		// add it
		resourceEnvironmentModel.getLinkingResources__ResourceEnvironment().add(link);
	}

	private CommunicationLinkResourceSpecification createDefaultLinkSpec() {
		CommunicationLinkResourceSpecification spec = ResourceenvironmentFactory.eINSTANCE
				.createCommunicationLinkResourceSpecification();
		spec.setFailureProbability(0.0d);
		spec.setLatency_CommunicationLinkResourceSpecification(PCMUtils.createRandomVariableFromString("0"));
		spec.setThroughput_CommunicationLinkResourceSpecification(PCMUtils.createRandomVariableFromString("1000"));
		return spec;
	}

}