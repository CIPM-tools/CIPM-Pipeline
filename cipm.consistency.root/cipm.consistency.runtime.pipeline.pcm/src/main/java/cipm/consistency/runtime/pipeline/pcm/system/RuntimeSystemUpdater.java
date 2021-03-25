package cipm.consistency.runtime.pipeline.pcm.system;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cipm.consistency.base.core.facade.pcm.IAllocationQueryFacade;
import cipm.consistency.base.core.facade.pcm.ISystemQueryFacade;
import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph;
import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge;
import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode;
import cipm.consistency.base.shared.pcm.util.deprecation.IDeprecationProcessor;
import cipm.consistency.base.shared.pcm.util.deprecation.SimpleDeprecationProcessor;
import cipm.consistency.runtime.pipeline.pcm.system.RuntimeSystemTransformation.CallGraphMergeMetadata;
import cipm.consistency.runtime.pipeline.pcm.system.RuntimeSystemTransformation.ComponentInterfaceBinding;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;

@Service
@Log
public class RuntimeSystemUpdater {
	@Autowired
	private ISystemQueryFacade systemQuery;

	@Autowired
	private IAllocationQueryFacade allocationQuery;

	private IDeprecationProcessor deprecationProcessorAssembly;
	private IDeprecationProcessor deprecationProcessorConnectors;

	public RuntimeSystemUpdater() {
		// => deprecation time of 1 is currently necessary
		// this could be adapted by putting more logic into the delegation of required
		// roles
		this.deprecationProcessorAssembly = new SimpleDeprecationProcessor(1);
		this.deprecationProcessorConnectors = new SimpleDeprecationProcessor(1);
	}

	public void applyCallGraph(Pair<ServiceCallGraph, CallGraphMergeMetadata> mergeResult) {
		// get call graph
		ServiceCallGraph scg = mergeResult.getLeft();

		// get metadata
		CallGraphMergeMetadata metadata = mergeResult.getRight();

		// get entry points
		List<ServiceCallGraphNode> entryPoints = getEntryPoints(scg);

		// process entry points
		for (ServiceCallGraphNode entryPoint : entryPoints) {
			// process
			processNode(entryPoint, scg, metadata);
		}

		// adapt links to outside
		updateSystemProvidedRoles(entryPoints, metadata);

		// deprecation processing
		removeUnusedConnectors(scg, metadata);
		systemQuery.processUnreachableAssemblys(deprecationProcessorAssembly);

		// get open required roles and delegate them
		updateSystemRequiredRoles();

		// finish iterations
		this.deprecationProcessorAssembly.iterationFinished();
		this.deprecationProcessorConnectors.iterationFinished();
	}

	private void removeUnusedConnectors(ServiceCallGraph scg, CallGraphMergeMetadata metadata) {
		List<Connector> connectorsToRemove = Lists.newArrayList();
		for (AssemblyConnector conn : systemQuery.getAssemblyConnectors()) {

			boolean hasBelongingEdge = scg.getEdges().stream().anyMatch(e -> {
				OperationRequiredRole requiredRole = e.getExternalCall().getRole_ExternalService();
				AssemblyContext correspondingACtxTarget = resolveCtx(
						e.getTo().getSeff().getBasicComponent_ServiceEffectSpecification(), e.getTo().getHost());

				OperationProvidedRole correspondingProvidedRole = getCorrespondingProvidedRole(requiredRole, e.getTo(),
						requiredRole.getRequiredInterface__OperationRequiredRole(), metadata);
				AssemblyContext correspondingACtxSource = resolveCtx(
						e.getFrom().getSeff().getBasicComponent_ServiceEffectSpecification(), e.getFrom().getHost());

				if (correspondingACtxTarget == null || correspondingACtxSource == null) {
					return false;
				}

				return conn.getProvidedRole_AssemblyConnector().getId().equals(correspondingProvidedRole.getId())
						&& conn.getProvidingAssemblyContext_AssemblyConnector().getId()
								.equals(correspondingACtxTarget.getId())
						&& conn.getRequiringAssemblyContext_AssemblyConnector().getId()
								.equals(correspondingACtxSource.getId())
						&& conn.getRequiredRole_AssemblyConnector().getId().equals(requiredRole.getId());
			});

			if (!hasBelongingEdge) {
				log.info("Found a deprecated connector.");
				if (deprecationProcessorConnectors.shouldDelete(conn)) {
					connectorsToRemove.add(conn);
				}
			}
		}

		log.info("Unused connectors that will be removed: " + connectorsToRemove.size());
		systemQuery.removeConnectors(connectorsToRemove);
	}

	private void updateSystemRequiredRoles() {
		List<Pair<AssemblyContext, OperationRequiredRole>> openInnerRequiredRoles = systemQuery
				.getUnsatisfiedInnerRequiredRoles();
		List<OperationRequiredRole> openOuterRequiredRoles = systemQuery.getNonLinkedOuterRequiredRoles();

		for (Pair<AssemblyContext, OperationRequiredRole> openInnerRequiredRole : openInnerRequiredRoles) {
			OperationRequiredRole selectedOuter = openOuterRequiredRoles.stream()
					.filter(rr -> rr.getRequiredInterface__OperationRequiredRole()
							.equals(openInnerRequiredRole.getRight().getRequiredInterface__OperationRequiredRole()))
					.findFirst().orElse(null);
			if (selectedOuter != null) {
				openOuterRequiredRoles.remove(selectedOuter);
				systemQuery.reconnectOuterRequiredRole(selectedOuter, openInnerRequiredRole.getLeft(),
						openInnerRequiredRole.getRight());
			}
		}
	}

	private void updateSystemProvidedRoles(List<ServiceCallGraphNode> entryPoints, CallGraphMergeMetadata metadata) {
		Map<String, PriorityQueue<AssemblyProvidedRoleBinding>> ifacePriorityMapping = extractEntryNodePriorityMapping(
				entryPoints, metadata);
		for (OperationProvidedRole systemProvidedRole : systemQuery.getProvidedRoles()) {
			OperationInterface belIface = systemProvidedRole.getProvidedInterface__OperationProvidedRole();
			if (ifacePriorityMapping.containsKey(belIface.getId())) {
				PriorityQueue<AssemblyProvidedRoleBinding> innerQueue = ifacePriorityMapping.get(belIface.getId());
				if (innerQueue.size() > 0) {
					AssemblyProvidedRoleBinding selectedRole = innerQueue.poll();
					if (selectedRole != null) {
						systemQuery.reconnectOuterProvidedRole(systemProvidedRole, selectedRole.ctx, selectedRole.role);
						continue;
					}
				}
			}

			// (maybe!) not satisfied
			if (systemQuery.getUnsatisfiedOuterProvidedRoles().contains(systemProvidedRole)) {
				log.warning("A provided role of the system may not be satisfied.");
			}
		}
	}

	private Map<String, PriorityQueue<AssemblyProvidedRoleBinding>> extractEntryNodePriorityMapping(
			List<ServiceCallGraphNode> entryPoints, CallGraphMergeMetadata metadata) {
		Map<String, PriorityQueue<AssemblyProvidedRoleBinding>> ifacePriorityMapping = Maps.newHashMap();
		for (ServiceCallGraphNode entryPoint : entryPoints) {
			AssemblyContext ctx = resolveOrCreateCtx(
					entryPoint.getSeff().getBasicComponent_ServiceEffectSpecification(), entryPoint.getHost());
			List<OperationProvidedRole> correspondingProvidedRoles = systemQuery.getProvidedRoleBySignature(
					entryPoint.getSeff().getDescribedService__SEFF(), ctx.getEncapsulatedComponent__AssemblyContext());
			OperationInterface correspondingInterface = ((OperationSignature) entryPoint.getSeff()
					.getDescribedService__SEFF()).getInterface__OperationSignature();

			if (correspondingProvidedRoles.size() > 0) {
				// add with regard to the priority

				// index of does not work here because of the equality properties of the pcm
				// elements
				int priority = indexOfNode(entryPoint, metadata.getEntryNodePriorities());

				for (OperationProvidedRole correspondingProvidedRole : correspondingProvidedRoles) {
					AssemblyProvidedRoleBinding innerBinding = new AssemblyProvidedRoleBinding(ctx,
							correspondingProvidedRole, priority);
					if (!ifacePriorityMapping.containsKey(correspondingInterface.getId())) {
						ifacePriorityMapping.put(correspondingInterface.getId(), new PriorityQueue<>());
					}

					PriorityQueue<AssemblyProvidedRoleBinding> correspondingPriorityQueue = ifacePriorityMapping
							.get(correspondingInterface.getId());
					if (!correspondingPriorityQueue.contains(innerBinding)) {
						correspondingPriorityQueue.add(innerBinding);
					}
				}
			}
		}
		return ifacePriorityMapping;
	}

	private int indexOfNode(ServiceCallGraphNode entryPoint, LinkedList<ServiceCallGraphNode> entryNodePriorities) {
		return IntStream.range(0, entryNodePriorities.size()).filter(i -> {
			ServiceCallGraphNode n = entryNodePriorities.get(i);
			if (n.getHost().getId().equals(entryPoint.getHost().getId())
					&& n.getSeff().getId().equals(entryPoint.getSeff().getId())) {
				return true;
			}
			return false;
		}).findFirst().orElse(-1);
	}

	private void processNode(ServiceCallGraphNode entryPoint, ServiceCallGraph scg, CallGraphMergeMetadata metadata) {
		// get corresponding context
		AssemblyContext correspondingACtx = resolveOrCreateCtx(
				entryPoint.getSeff().getBasicComponent_ServiceEffectSpecification(), entryPoint.getHost());

		for (ServiceCallGraphEdge outgoingEdge : scg.getOutgoingEdges().get(entryPoint)) {
			OperationRequiredRole requiredRole = outgoingEdge.getExternalCall().getRole_ExternalService();
			AssemblyContext correspondingACtxTarget = resolveOrCreateCtx(
					outgoingEdge.getTo().getSeff().getBasicComponent_ServiceEffectSpecification(),
					outgoingEdge.getTo().getHost());

			OperationProvidedRole correspondingProvidedRole = getCorrespondingProvidedRole(requiredRole,
					outgoingEdge.getTo(), requiredRole.getRequiredInterface__OperationRequiredRole(), metadata);

			if (correspondingProvidedRole != null) {
				// => prioritized
				if (!systemQuery.hasConnector(correspondingACtx, requiredRole, correspondingACtxTarget,
						correspondingProvidedRole)) {
					// => new connector
					AssemblyConnector connector = systemQuery.createConnector(correspondingACtx, requiredRole,
							correspondingACtxTarget, correspondingProvidedRole);

					// remove inconsistent ones
					systemQuery.removeInconsistentConnectors(connector);
				}
			}

			// process recursive
			processNode(outgoingEdge.getTo(), scg, metadata);
		}
	}

	private OperationProvidedRole getCorrespondingProvidedRole(OperationRequiredRole requiredRole,
			ServiceCallGraphNode to, OperationInterface iface, CallGraphMergeMetadata metadata) {
		ComponentInterfaceBinding binding = new ComponentInterfaceBinding(
				to.getSeff().getBasicComponent_ServiceEffectSpecification(), to.getHost(), iface);
		if (metadata.getBindingPriorityMap().containsKey(binding)) {
			int fIndex = metadata.getBindingPriorityMap().get(binding).indexOf(requiredRole);
			List<OperationProvidedRole> openRoles = getProvidedRoleAmount(binding.getComponent(), iface);
			if (openRoles.size() > 0) {
				return openRoles.get(fIndex % openRoles.size());
			}
		}

		return null;
	}

	private List<OperationProvidedRole> getProvidedRoleAmount(BasicComponent component, OperationInterface iface) {
		return component.getProvidedRoles_InterfaceProvidingEntity().stream().filter(i -> {
			return i instanceof OperationProvidedRole
					&& ((OperationProvidedRole) i).getProvidedInterface__OperationProvidedRole().equals(iface);
		}).map(OperationProvidedRole.class::cast).collect(Collectors.toList());
	}

	private AssemblyContext resolveCtx(BasicComponent basicComponent, ResourceContainer host) {
		List<AssemblyContext> existingContexts = allocationQuery.getDeployedAssembly(basicComponent, host);
		if (existingContexts.size() == 0) {
			return null;
		} else {
			return existingContexts.get(0);
		}
	}

	private AssemblyContext resolveOrCreateCtx(BasicComponent basicComponent, ResourceContainer host) {
		List<AssemblyContext> existingContexts = allocationQuery.getDeployedAssembly(basicComponent, host);

		AssemblyContext correspondingCtx = null;
		if (existingContexts.size() == 0) {
			correspondingCtx = systemQuery.createAssemblyContext(basicComponent);
			allocationQuery.deployAssembly(correspondingCtx, host);
		} else if (existingContexts.size() == 1) {
			correspondingCtx = existingContexts.get(0);
		}

		if (correspondingCtx == null) {
			log.severe(
					"There are multiple assembly contexts of the same component type deployed on the same container. This breaks an assumption and therefore the system updates could not be performed.");
			return null;
		}
		return correspondingCtx;
	}

	private List<ServiceCallGraphNode> getEntryPoints(ServiceCallGraph scg) {
		return scg.getNodes().stream().filter(n -> scg.getIncomingEdges().get(n).size() == 0)
				.collect(Collectors.toList());
	}

	@Data
	@AllArgsConstructor
	private static class AssemblyProvidedRoleBinding implements Comparable<AssemblyProvidedRoleBinding> {
		private AssemblyContext ctx;
		private OperationProvidedRole role;

		@EqualsExclude
		private int priority;

		@Override
		public int compareTo(AssemblyProvidedRoleBinding o) {
			return Integer.valueOf(priority).compareTo(o.priority);
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof AssemblyProvidedRoleBinding) {
				AssemblyProvidedRoleBinding _other = (AssemblyProvidedRoleBinding) other;
				return _other.ctx.getId().equals(ctx.getId()) && _other.role.getId().equals(role.getId());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(ctx.getId(), role.getId());
		}
	}

}
