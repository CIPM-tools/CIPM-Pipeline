package cipm.consistency.tools.evaluation.scenario.data.scenarios;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.allocation.AllocationFactory;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.resourceenvironment.CommunicationLinkResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.base.shared.pcm.util.PCMUtils;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenario;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioExecutionConfig;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioType;
import cipm.consistency.tools.evaluation.scenario.data.teastore.ReplicationComponentType;
import lombok.Data;
import lombok.EqualsAndHashCode;

// also covers dereplication
@Data
@EqualsAndHashCode(callSuper = true)
public class ReplicationScenario extends AdaptionScenario {

	private ReplicationComponentType component;
	private int newAmount;

	public ReplicationScenario(ReplicationComponentType type, int newAmount) {
		super(AdaptionScenarioType.REPLICATION);
		this.component = type;
		this.newAmount = newAmount;
	}

	public ReplicationScenario() {
		this(null, 0);
	}

	@Override
	public void execute(AdaptionScenarioExecutionConfig config) {
		ProcessBuilder builder = new ProcessBuilder("docker-compose", "scale",
				component.getName() + "=" + String.valueOf(newAmount));
		builder.directory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
		try {
			builder.start().waitFor();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public InMemoryPCM generateReferenceModel(InMemoryPCM current) {
		InMemoryPCM copy = current.copyDeep();
		if (this.component == ReplicationComponentType.IMAGE) {
			return copy;
		}

		// first search for the component instances (assemblys)
		int count = 0;
		AssemblyContext representCtx = null;
		for (AssemblyContext ctx : copy.getSystem().getAssemblyContexts__ComposedStructure()) {
			if (ctx.getEncapsulatedComponent__AssemblyContext().getId().equals(component.getId())) {
				count++;
				representCtx = ctx;
			}
		}

		// corresponding component
		BasicComponent comp = PCMUtils.getElementById(copy.getRepository(), BasicComponent.class, component.getId());

		// prepare
		// 4.1. search belonging registry that is connected
		String representCtxId = representCtx.getId();
		AssemblyConnector registryConnector = copy.getSystem().getConnectors__ComposedStructure().stream()
				.filter(c -> c instanceof AssemblyConnector).map(AssemblyConnector.class::cast)
				.filter(c -> c.getProvidingAssemblyContext_AssemblyConnector().getId().equals(representCtxId))
				.findFirst().orElse(null);
		AssemblyContext registryCtx = registryConnector.getRequiringAssemblyContext_AssemblyConnector();

		// 4.2. get required roles of registry matching
		Set<String> providedInterfaceIds = Sets.newHashSet();
		comp.getProvidedRoles_InterfaceProvidingEntity().stream().filter(r -> r instanceof OperationProvidedRole)
				.map(OperationProvidedRole.class::cast)
				.forEach(r -> providedInterfaceIds.add(r.getProvidedInterface__OperationProvidedRole().getId()));

		List<OperationRequiredRole> registryRequiredRoles = registryCtx.getEncapsulatedComponent__AssemblyContext()
				.getRequiredRoles_InterfaceRequiringEntity().stream().filter(r -> r instanceof OperationRequiredRole)
				.map(OperationRequiredRole.class::cast)
				.filter(r -> providedInterfaceIds.contains(r.getRequiredInterface__OperationRequiredRole().getId()))
				.collect(Collectors.toList());

		// then we need to create new ones
		if (count < newAmount) {
			for (int i = 0; i < newAmount - count; i++) {
				// 1. we need a new container
				ResourceContainer emptyContainer = buildNewDefaultContainer();
				copy.getResourceEnvironmentModel().getResourceContainer_ResourceEnvironment().add(emptyContainer);

				// 2. create the assembly
				AssemblyContext nCtx = CompositionFactory.eINSTANCE.createAssemblyContext();
				nCtx.setEncapsulatedComponent__AssemblyContext(comp);
				copy.getSystem().getAssemblyContexts__ComposedStructure().add(nCtx);

				// 3. create the allocation context
				AllocationContext ACtx = AllocationFactory.eINSTANCE.createAllocationContext();
				ACtx.setAssemblyContext_AllocationContext(nCtx);
				ACtx.setResourceContainer_AllocationContext(emptyContainer);
				copy.getAllocationModel().getAllocationContexts_Allocation().add(ACtx);

				// 4. connect the assembly and the containers

				// 4.3. filter for open required roles of registry
				copy.getSystem().getConnectors__ComposedStructure().stream().filter(c -> c instanceof AssemblyConnector)
						.map(AssemblyConnector.class::cast).filter(c -> {
							return c.getRequiringAssemblyContext_AssemblyConnector().getId().equals(registryCtx.getId())
									&& registryRequiredRoles.contains(c.getRequiredRole_AssemblyConnector());
						}).map(c -> c.getRequiredRole_AssemblyConnector())
						.forEach(r -> registryRequiredRoles.remove(r));

				// 4.4. connect assembly
				AssemblyConnector nConnector = CompositionFactory.eINSTANCE.createAssemblyConnector();
				nConnector.setProvidingAssemblyContext_AssemblyConnector(nCtx);
				nConnector.setRequiringAssemblyContext_AssemblyConnector(registryCtx);
				nConnector.setRequiredRole_AssemblyConnector(registryRequiredRoles.get(0));
				// in our case possible
				nConnector.setProvidedRole_AssemblyConnector(
						(OperationProvidedRole) comp.getProvidedRoles_InterfaceProvidingEntity().get(0));

				// 4.4.1. connect assembly on the other side
				OperationRequiredRole registryReqRole = comp.getRequiredRoles_InterfaceRequiringEntity().stream()
						.filter(f -> f instanceof OperationRequiredRole).map(OperationRequiredRole.class::cast)
						.filter(opr -> opr.getRequiredInterface__OperationRequiredRole().getId()
								.equals("_gGczsDVZEeqPG_FgW3bi6Q"))
						.findFirst().orElse(null);
				if (registryReqRole != null) {
					OperationProvidedRole loadBalancerProvidingRole = (OperationProvidedRole) registryCtx
							.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity()
							.get(0);

					AssemblyConnector nConnectorInner = CompositionFactory.eINSTANCE.createAssemblyConnector();
					nConnectorInner.setProvidingAssemblyContext_AssemblyConnector(registryCtx);
					nConnectorInner.setRequiringAssemblyContext_AssemblyConnector(nCtx);
					nConnectorInner.setRequiredRole_AssemblyConnector(registryReqRole);
					// in our case possible
					nConnectorInner.setProvidedRole_AssemblyConnector(loadBalancerProvidingRole);

					copy.getSystem().getConnectors__ComposedStructure().add(nConnectorInner);
				}

				// 4.5. add it
				copy.getSystem().getConnectors__ComposedStructure().add(nConnector);

				// 4.6. search for container of registry
				AllocationContext registryACtx = copy.getAllocationModel().getAllocationContexts_Allocation().stream()
						.filter(ac -> ac.getAssemblyContext_AllocationContext().getId().equals(registryCtx.getId()))
						.findFirst().orElse(null);

				// 4.7. create connection
				LinkingResource linkRes = buildNewDefaultLink();
				linkRes.getConnectedResourceContainers_LinkingResource().add(emptyContainer);
				linkRes.getConnectedResourceContainers_LinkingResource()
						.add(registryACtx.getResourceContainer_AllocationContext());
			}
		} else if (count > newAmount) {
			// remove last assembly that is connected to registry
			// 4.3. filter for closed required roles of registry
			List<AssemblyConnector> connectorsToRemove = Lists.newArrayList();
			Set<String> assemblyIdsToRemove = Sets.newHashSet();

			for (int i = count - 1; i >= newAmount; i--) {
				OperationRequiredRole roleToFree = registryRequiredRoles.get(i);
				for (AssemblyConnector connector : copy.getSystem().getConnectors__ComposedStructure().stream()
						.filter(conn -> conn instanceof AssemblyConnector).map(AssemblyConnector.class::cast)
						.collect(Collectors.toList())) {
					if (connector.getRequiredRole_AssemblyConnector().getId().equals(roleToFree.getId())) {
						connectorsToRemove.add(connector);
						assemblyIdsToRemove.add(connector.getProvidingAssemblyContext_AssemblyConnector().getId());
					}
				}
			}

			for (AssemblyConnector toRemove : connectorsToRemove) {
				copy.getSystem().getConnectors__ComposedStructure().remove(toRemove);
				copy.getSystem().getAssemblyContexts__ComposedStructure()
						.remove(toRemove.getProvidingAssemblyContext_AssemblyConnector());

				AllocationContext ctxToRemove = copy.getAllocationModel().getAllocationContexts_Allocation().stream()
						.filter(actx -> actx.getAssemblyContext_AllocationContext().getId()
								.equals(toRemove.getProvidingAssemblyContext_AssemblyConnector().getId()))
						.findFirst().orElse(null);
				if (ctxToRemove != null) {
					copy.getAllocationModel().getAllocationContexts_Allocation().remove(ctxToRemove);
				}
			}
			connectorsToRemove.clear();

			for (AssemblyConnector connector : copy.getSystem().getConnectors__ComposedStructure().stream()
					.filter(conn -> conn instanceof AssemblyConnector).map(AssemblyConnector.class::cast)
					.collect(Collectors.toList())) {
				if (assemblyIdsToRemove.contains(connector.getProvidingAssemblyContext_AssemblyConnector().getId())
						|| assemblyIdsToRemove
								.contains(connector.getRequiringAssemblyContext_AssemblyConnector().getId())) {
					connectorsToRemove.add(connector);
				}
			}

			for (AssemblyConnector toRemove : connectorsToRemove) {
				copy.getSystem().getConnectors__ComposedStructure().remove(toRemove);
			}

		}

		return copy;
	}

	private LinkingResource buildNewDefaultLink() {
		LinkingResource output = ResourceenvironmentFactory.eINSTANCE.createLinkingResource();
		CommunicationLinkResourceSpecification spec = ResourceenvironmentFactory.eINSTANCE
				.createCommunicationLinkResourceSpecification();
		spec.setFailureProbability(0);
		spec.setLatency_CommunicationLinkResourceSpecification(PCMUtils.createRandomVariableFromString("0"));
		spec.setThroughput_CommunicationLinkResourceSpecification(PCMUtils.createRandomVariableFromString("1"));
		output.setCommunicationLinkResourceSpecifications_LinkingResource(spec);
		return output;
	}

	private ResourceContainer buildNewDefaultContainer() {
		ProcessingResourceType CPU = PCMUtils.getElementById(PCMUtils.getDefaultResourceRepository(),
				ProcessingResourceType.class, "_oro4gG3fEdy4YaaT-RYrLQ");
		ProcessingResourceType HDD = PCMUtils.getElementById(PCMUtils.getDefaultResourceRepository(),
				ProcessingResourceType.class, "_BIjHoQ3KEdyouMqirZIhzQ");

		ResourceContainer output = ResourceenvironmentFactory.eINSTANCE.createResourceContainer();

		// CPU
		ProcessingResourceSpecification cpuSpec = ResourceenvironmentFactory.eINSTANCE
				.createProcessingResourceSpecification();
		cpuSpec.setActiveResourceType_ActiveResourceSpecification(CPU);
		fillOutSpec(cpuSpec);

		// HDD
		ProcessingResourceSpecification hddSpec = ResourceenvironmentFactory.eINSTANCE
				.createProcessingResourceSpecification();
		hddSpec.setActiveResourceType_ActiveResourceSpecification(HDD);
		fillOutSpec(hddSpec);

		// add specs
		output.getActiveResourceSpecifications_ResourceContainer().add(cpuSpec);
		output.getActiveResourceSpecifications_ResourceContainer().add(hddSpec);

		return output;
	}

	private void fillOutSpec(ProcessingResourceSpecification cpuSpec) {
		cpuSpec.setNumberOfReplicas(1);
		cpuSpec.setProcessingRate_ProcessingResourceSpecification(PCMUtils.createRandomVariableFromString("1"));
		cpuSpec.setMTTF(0);
		cpuSpec.setMTTR(0);
	}

}
