package dmodel.pipeline.rt.pcm.system;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import dmodel.pipeline.core.facade.pcm.IAllocationQueryFacade;
import dmodel.pipeline.core.facade.pcm.ISystemQueryFacade;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode;
import dmodel.pipeline.rt.pcm.system.RuntimeSystemTransformation.CallGraphMergeMetadata;
import dmodel.pipeline.rt.pcm.system.RuntimeSystemTransformation.ComponentInterfaceBinding;
import dmodel.pipeline.shared.pcm.util.deprecation.IDeprecationProcessor;
import dmodel.pipeline.shared.pcm.util.deprecation.SimpleDeprecationProcessor;
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

	private IDeprecationProcessor deprecationProcessor;

	public RuntimeSystemUpdater() {
		// => deprecation time of 1 is currently necessary
		// this could be adapted by putting more logic into the delegation of required
		// roles
		this.deprecationProcessor = new SimpleDeprecationProcessor(1);
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
		systemQuery.processUnreachableAssemblys(deprecationProcessor);

		// get open required roles and delegate them
		updateSystemRequiredRoles();
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
		Map<OperationInterface, PriorityQueue<AssemblyProvidedRoleBinding>> ifacePriorityMapping = extractEntryNodePriorityMapping(
				entryPoints, metadata);
		for (OperationProvidedRole systemProvidedRole : systemQuery.getProvidedRoles()) {
			OperationInterface belIface = systemProvidedRole.getProvidedInterface__OperationProvidedRole();
			if (ifacePriorityMapping.containsKey(belIface)) {
				PriorityQueue<AssemblyProvidedRoleBinding> innerQueue = ifacePriorityMapping.get(belIface);
				if (innerQueue.size() > 0) {
					AssemblyProvidedRoleBinding selectedRole = ifacePriorityMapping.get(belIface).remove();
					systemQuery.reconnectOuterProvidedRole(systemProvidedRole, selectedRole.ctx, selectedRole.role);
				} else {
					log.warning("A provided role of the system can not be satisfied.");
				}
			} else {
				log.warning("A provided role of the system can not be satisfied.");
			}
		}
	}

	private Map<OperationInterface, PriorityQueue<AssemblyProvidedRoleBinding>> extractEntryNodePriorityMapping(
			List<ServiceCallGraphNode> entryPoints, CallGraphMergeMetadata metadata) {
		Map<OperationInterface, PriorityQueue<AssemblyProvidedRoleBinding>> ifacePriorityMapping = Maps.newHashMap();
		for (ServiceCallGraphNode entryPoint : entryPoints) {
			AssemblyContext ctx = resolveOrCreateCtx(
					entryPoint.getSeff().getBasicComponent_ServiceEffectSpecification(), entryPoint.getHost());
			List<OperationProvidedRole> correspondingProvidedRoles = systemQuery.getProvidedRoleBySignature(
					entryPoint.getSeff().getDescribedService__SEFF(), ctx.getEncapsulatedComponent__AssemblyContext());
			OperationInterface correspondingInterface = ((OperationSignature) entryPoint.getSeff()
					.getDescribedService__SEFF()).getInterface__OperationSignature();
			if (correspondingProvidedRoles.size() > 0) {
				// add with regard to the priority
				int priority = metadata.getEntryNodePriorities().indexOf(entryPoint);
				for (OperationProvidedRole correspondingProvidedRole : correspondingProvidedRoles) {
					AssemblyProvidedRoleBinding innerBinding = new AssemblyProvidedRoleBinding(ctx,
							correspondingProvidedRole, priority);
					if (!ifacePriorityMapping.containsKey(correspondingInterface)) {
						ifacePriorityMapping.put(correspondingInterface, new PriorityQueue<>());
					}

					PriorityQueue<AssemblyProvidedRoleBinding> correspondingPriorityQueue = ifacePriorityMapping
							.get(correspondingInterface);
					if (!correspondingPriorityQueue.contains(innerBinding)) {
						correspondingPriorityQueue.add(innerBinding);
					}
				}
			}
		}
		return ifacePriorityMapping;
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

			OperationProvidedRole correspondingProvidedRole = isPrioritized(requiredRole, outgoingEdge.getTo(),
					requiredRole.getRequiredInterface__OperationRequiredRole(), metadata);

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

	private OperationProvidedRole isPrioritized(OperationRequiredRole requiredRole, ServiceCallGraphNode to,
			OperationInterface iface, CallGraphMergeMetadata metadata) {
		ComponentInterfaceBinding binding = new ComponentInterfaceBinding(
				to.getSeff().getBasicComponent_ServiceEffectSpecification(), to.getHost(), iface);
		if (metadata.getBindingPriorityMap().containsKey(binding)) {
			int fIndex = metadata.getBindingPriorityMap().get(binding).indexOf(requiredRole);
			List<OperationProvidedRole> openRoles = getProvidedRoleAmount(binding.getComponent(), iface);
			if (fIndex >= 0 && fIndex < openRoles.size()) {
				return openRoles.get(fIndex);
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
	}

}
