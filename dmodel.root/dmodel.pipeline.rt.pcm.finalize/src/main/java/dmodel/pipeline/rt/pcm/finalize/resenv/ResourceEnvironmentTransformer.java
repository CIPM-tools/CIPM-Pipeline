package dmodel.pipeline.rt.pcm.finalize.resenv;

import java.util.Arrays;
import java.util.Optional;

import org.palladiosimulator.pcm.resourceenvironment.CommunicationLinkResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dmodel.pipeline.dt.mmmodel.MeasurementModel;
import dmodel.pipeline.models.mapping.HostIDMapping;
import dmodel.pipeline.models.mapping.MappingFactory;
import dmodel.pipeline.models.mapping.PalladioRuntimeMapping;
import dmodel.pipeline.rt.pcm.finalize.IMeasurementModelProcessor;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pcm.PCMUtils;

@Service
public class ResourceEnvironmentTransformer implements IMeasurementModelProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(ResourceEnvironmentTransformer.class);

	@Override
	public void processMeasurementModel(MeasurementModel mm, InMemoryPCM pcm, PalladioRuntimeMapping mapping) {
		processHosts(mm, pcm, mapping);
		processLinks(mm, pcm, mapping);
	}

	private void processLinks(MeasurementModel mm, InMemoryPCM pcm, PalladioRuntimeMapping mapping) {
		mm.getEnvironmentData().getConnections().stream().forEach(link -> {
			Optional<HostIDMapping> hostFrom = mapping.getHostMappings().stream()
					.filter(mp -> mp.getHostID().equals(link.getLeft())).findFirst();
			Optional<HostIDMapping> hostTo = mapping.getHostMappings().stream()
					.filter(mp -> mp.getHostID().equals(link.getRight())).findFirst();

			if (hostFrom.isPresent() && hostTo.isPresent()) {
				Optional<LinkingResource> belLink = getLinkingResource(pcm.getResourceEnvironmentModel(),
						hostFrom.get().getPcmContainerID(), hostTo.get().getPcmContainerID());
				if (!belLink.isPresent()) {
					// create link
					createLink(pcm.getResourceEnvironmentModel(), hostFrom.get().getPcmContainerID(),
							hostTo.get().getPcmContainerID());
				}
			} else {
				LOG.warn("Updating the Resource Environment failed due to mapping problems.");
			}
		});
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

	private void processHosts(MeasurementModel mm, InMemoryPCM pcm, PalladioRuntimeMapping mapping) {
		mm.getEnvironmentData().getHosts().stream().forEach(host -> {
			Optional<HostIDMapping> refMapping = mapping.getHostMappings().stream()
					.filter(mp -> mp.getHostID().equals(host.getHostId())).findFirst();

			if (refMapping.isPresent()) {
				ResourceContainer refContainer = PCMUtils.getElementById(pcm.getResourceEnvironmentModel(),
						ResourceContainer.class, refMapping.get().getPcmContainerID());

				if (refContainer == null) {
					// create it
					HostIDMapping resMapping = createContainer(pcm.getResourceEnvironmentModel(), host.getHostId(),
							host.getHostName());
					mapping.getHostMappings().add(resMapping);
				}
			} else {
				HostIDMapping resMapping = createContainer(pcm.getResourceEnvironmentModel(), host.getHostId(),
						host.getHostName());
				mapping.getHostMappings().add(resMapping);
			}
		});
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
		LOG.info("Created new resource environment container (name: '" + hostName + "').");

		return nMapping;
	}

	private ProcessingResourceSpecification createDefaultCPUSpecficiation() {
		ProcessingResourceSpecification spec = ResourceenvironmentFactory.eINSTANCE
				.createProcessingResourceSpecification();
		spec.setMTTF(0);
		spec.setMTTR(0);
		spec.setNumberOfReplicas(0);
		spec.setProcessingRate_ProcessingResourceSpecification(PCMUtils.createRandomVariableFromString("1"));

		// backlink
		spec.getProcessingRate_ProcessingResourceSpecification()
				.setProcessingResourceSpecification_processingRate_PCMRandomVariable(spec);
		// TODO spec.setSchedulingPolicy(SchedulingPolicy.);
		return spec;
	}

	private Optional<LinkingResource> getLinkingResource(ResourceEnvironment env, String... ids) {
		return PCMUtils.getElementsByType(env, LinkingResource.class).stream().filter(lr -> {
			return ids.length == lr.getConnectedResourceContainers_LinkingResource().size()
					&& lr.getConnectedResourceContainers_LinkingResource().stream()
							.allMatch(r -> Arrays.stream(ids).anyMatch(r.getId()::equals));
		}).findFirst();
	}

}
