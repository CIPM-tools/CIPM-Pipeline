package dmodel.pipeline.dt.system.pcm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ComposedStructure;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingEntity;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.system.SystemFactory;
import org.springframework.stereotype.Component;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode;
import dmodel.pipeline.dt.system.pcm.IAssemblySelectionListener;
import dmodel.pipeline.dt.system.pcm.IConnectionConflictListener;
import dmodel.pipeline.dt.system.pcm.data.AbstractConflict;
import dmodel.pipeline.dt.system.pcm.data.AssemblyConflict;
import dmodel.pipeline.dt.system.pcm.data.ConnectionConflict;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
import lombok.extern.java.Log;

// TODO simplify building method
// TODO add logging
// TODO outsource in other helping classes
// TODO use PCMSystemUtil
@Component
@Log
public class PCMSystemBuilder {
	private long conflictCounter = 0;
	private List<IConnectionConflictListener> connectionConflictListener;
	private List<IAssemblySelectionListener> assemblySelectionListener;

	// current data
	private AbstractConflict<?> currentConflict;
	private List<AssemblyProvidedRole> openProvidedRoles;
	private System currentSystem;
	private Iterator<ServiceCallGraphNode> entryPoints;
	private LinkedList<List<AssemblyEdge>> currentEdges; // clustered over signature

	private List<AssemblyEdge> currentEdge;
	private ServiceCallGraph currentServiceCallGraph;
	private Map<ProvidedRole, RequiredRole> provReqMapping;
	private Map<RequiredRole, ProvidedRole> reqProvMapping;
	private ComposedStructure currentOuterStructure;

	private int currentAssemblyID;

	public PCMSystemBuilder() {
		this.assemblySelectionListener = new ArrayList<>();
		this.connectionConflictListener = new ArrayList<>();
	}

	public boolean startBuildingSystem(ServiceCallGraph serviceCallGraph) {
		// 0. create output system
		currentAssemblyID = 1;
		currentSystem = SystemFactory.eINSTANCE.createSystem();
		currentOuterStructure = currentSystem;

		// 1.1. find entry points to the call graph
		entryPoints = serviceCallGraph.getNodes().stream()
				.filter(n -> serviceCallGraph.getIncomingEdges().get(n) == null)
				.collect(Collectors.toCollection(LinkedHashSet::new)).iterator();

		// 1.2. containers
		openProvidedRoles = Lists.newArrayList();
		currentEdges = Lists.newLinkedList();

		// 1.3. default values
		currentConflict = null;
		currentServiceCallGraph = serviceCallGraph;
		provReqMapping = Maps.newHashMap();
		reqProvMapping = Maps.newHashMap();
		currentEdge = null;

		// 2. try to build a system and corresponding conflicts that need to be
		// resolved
		return buildingStep();
	}

	public boolean continueBuilding() {
		if (currentConflict != null && currentConflict.isSolved()) {
			if (currentConflict instanceof ConnectionConflict) {
				return resolveConnectionConflict((ConnectionConflict) currentConflict);
			} else if (currentConflict instanceof AssemblyConflict) {
				return resolveAssemblyConflict((AssemblyConflict) currentConflict);
			}
		} else {
			throw new IllegalStateException("Cannot continue if there is no resolved conflict.");
		}
		return false;
	}

	private boolean resolveAssemblyConflict(AssemblyConflict currentConflict) {
		if (currentConflict.getReqRole() == null) {
			clusterAndAddOutgoingEdges(currentConflict.getServiceTo(), currentConflict.getSolution());
		} else {
			AssemblyContext currentCtx = currentEdge.get(0).assemblyFrom;
			OperationRequiredRole reqRole = (OperationRequiredRole) currentConflict.getReqRole();
			ProvidedRole provRole = currentConflict.getSolution().getEncapsulatedComponent__AssemblyContext()
					.getProvidedRoles_InterfaceProvidingEntity().stream().filter(pr -> {
						if (pr instanceof OperationProvidedRole) {
							OperationProvidedRole opr = (OperationProvidedRole) pr;
							return opr.getProvidedInterface__OperationProvidedRole().getId()
									.equals(reqRole.getRequiredInterface__OperationRequiredRole().getId());
						}
						return false;
					}).findFirst().get();

			// link the assembly
			linkAssemblys(currentCtx, reqRole, currentConflict.getSolution(), provRole);

			// add outgoing edges
			clusterAndAddOutgoingEdges(currentConflict.getServiceTo(), currentConflict.getSolution());
		}
		// we solved the conflict
		currentConflict = null;

		return buildingStep();
	}

	private boolean resolveConnectionConflict(ConnectionConflict conf) {
		Optional<AssemblyEdge> selectedEdge = currentEdge.stream().filter(t -> {
			return t.assemblyFrom.getEncapsulatedComponent__AssemblyContext()
					.getRequiredRoles_InterfaceRequiringEntity().stream()
					.anyMatch(r -> conf.getRequired().getId().equals(r.getId()));
		}).findFirst();
		if (!selectedEdge.isPresent()) {
			log.log(Level.WARNING,
					"Could not resolve the current conflict because the required role could not be found.");
			return false;
		}
		// process the edge
		AssemblyContext belAssembly = processSingleEdge(selectedEdge.get(), conf.getRequired(), conf.getSolution());
		if (belAssembly == null) {
			// conflict
			return false;
		}

		// add outgoing edges
		clusterAndAddOutgoingEdges(selectedEdge.get().serviceTo, belAssembly);

		// we solved the conflict
		currentConflict = null;

		// recursion step
		return buildingStep();
	}

	private boolean buildingStep() {
		log.info("Executing a building step.");
		// if there is a conflict we cannot do anything
		if (currentConflict != null) {
			return false;
		}

		if (currentEdges.isEmpty()) {
			// we search for an entry point
			if (!entryPoints.hasNext()) {
				log.info("Finished the building process and linking free roles to the system border.");
				linkFreeRolesToSystemBorder();
				// we are finished
				return true;
			} else {
				// pop an entry point and get all edges
				ServiceCallGraphNode node = entryPoints.next();
				// collect them and put to the list
				AssemblyContext ctx = resolveAssemblyForEntryPoint(node.getSeff());

				if (ctx == null) {
					log.log(Level.WARNING, "Assembly for an entry point was null.");
					return false;
				}

				log.info("Adding outgoing edges of entry point '" + node.getSeff().getId() + "'.");
				clusterAndAddOutgoingEdges(node.getSeff(), ctx);

				// recursion -> next step
				return buildingStep();
			}
		} else {
			// pop a edge and process it
			currentEdge = currentEdges.removeFirst();
			return processEdgeSet();
		}
	}

	private void linkFreeRolesToSystemBorder() {
		for (AssemblyProvidedRole freeRole : openProvidedRoles) {
			if (freeRole.ctx.getParentStructure__AssemblyContext().getId().equals(currentSystem.getId())) {
				// it is directly under the parent
				log.info("Link free provided role to the system border.");

				if (freeRole.role instanceof OperationProvidedRole) {
					OperationProvidedRole innerProvided = (OperationProvidedRole) freeRole.role;

					OperationProvidedRole nProvided = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
					nProvided.setProvidingEntity_ProvidedRole(currentSystem);
					nProvided.setProvidedInterface__OperationProvidedRole(
							innerProvided.getProvidedInterface__OperationProvidedRole());
					currentSystem.getProvidedRoles_InterfaceProvidingEntity().add(nProvided);

					// delegation now
					ProvidedDelegationConnector nConnector = CompositionFactory.eINSTANCE
							.createProvidedDelegationConnector();
					nConnector.setOuterProvidedRole_ProvidedDelegationConnector(nProvided);
					nConnector.setInnerProvidedRole_ProvidedDelegationConnector(innerProvided);
					nConnector.setAssemblyContext_ProvidedDelegationConnector(freeRole.ctx);
					nConnector.setParentStructure__Connector(currentSystem);
					currentSystem.getConnectors__ComposedStructure().add(nConnector);
				}
			}
		}
	}

	private boolean processEdgeSet() {
		log.info("Processing set of edges with size " + currentEdge.size() + ".");
		if (currentEdge.size() > 0) {
			// resolve corresponding seffs
			AssemblyEdge firstEdge = currentEdge.get(0);
			ResourceDemandingSEFF from = firstEdge.serviceFrom; // for all in the set equal
			Signature toSig = firstEdge.serviceTo.getDescribedService__SEFF();

			// get components for this seff
			BasicComponent cfrom = from.getBasicComponent_ServiceEffectSpecification();

			// get the required role
			Optional<RequiredRole> reqRole = PCMUtils.getRequiredRoleBySignature(cfrom, toSig);

			if (!reqRole.isPresent()) {
				throw new RuntimeException(
						"Could not find required role for a specific service call. Please check the consistency of your repository model.");
			}

			// edges > 1?
			if (currentEdge.size() > 1) {
				log.info("Found edge conflict. Please resolve and continue the building prodcedure.");
				createEdgeConflict(currentEdge, reqRole.get(), toSig);
				return false;
			}

			// process the edge
			AssemblyContext belAssembly = processSingleEdge(currentEdge.get(0), reqRole.get(), toSig);
			if (belAssembly == null) {
				// conflict
				return false;
			}

			// add outgoing edges
			clusterAndAddOutgoingEdges(firstEdge.serviceTo, belAssembly);

			// recursion step
			return buildingStep();
		} else {
			return buildingStep();
		}
	}

	private AssemblyContext processSingleEdge(AssemblyEdge edge, RequiredRole reqRole, Signature callSig) {
		ResourceDemandingSEFF linkTo = edge.serviceTo;
		Optional<ProvidedRole> provRole = PCMUtils
				.getProvidedRoleBySignature(linkTo.getBasicComponent_ServiceEffectSpecification(), callSig);

		return processSingleEdge(edge, reqRole, provRole.get());
	}

	private AssemblyContext processSingleEdge(AssemblyEdge edge, RequiredRole reqRole, ProvidedRole provRole) {
		// resolve assembly for service
		AssemblyContext resolvedContext = resolveAssemblyFor(edge, reqRole, provRole);
		if (resolvedContext == null) {
			return null;
		}

		// link the assembly
		linkAssemblys(edge.assemblyFrom, reqRole, resolvedContext, provRole);

		return resolvedContext;
	}

	private void linkAssemblys(AssemblyContext assemblyFrom, RequiredRole reqRole, AssemblyContext resolvedContext,
			ProvidedRole provRole) {
		if (reqRole instanceof OperationRequiredRole && provRole instanceof OperationProvidedRole) {
			AssemblyConnector connector = CompositionFactory.eINSTANCE.createAssemblyConnector();
			connector.setProvidedRole_AssemblyConnector((OperationProvidedRole) provRole);
			connector.setProvidingAssemblyContext_AssemblyConnector(resolvedContext);
			connector.setParentStructure__Connector(currentOuterStructure);
			connector.setRequiredRole_AssemblyConnector((OperationRequiredRole) reqRole);
			connector.setRequiringAssemblyContext_AssemblyConnector(assemblyFrom);
			currentOuterStructure.getConnectors__ComposedStructure().add(connector);

			// map the roles
			provReqMapping.put(provRole, reqRole);
			reqProvMapping.put(reqRole, provRole);

			// remove the provided
			openProvidedRoles = openProvidedRoles.parallelStream().filter(op -> {
				if (op.ctx.getId().equals(resolvedContext.getId()) && op.role.getId().equals(provRole.getId())) {
					return false;
				}
				return true;
			}).collect(Collectors.toList());
		}
	}

	private AssemblyContext resolveAssemblyForEntryPoint(ResourceDemandingSEFF seff) {
		// search for matching assemblies
		List<AssemblyProvidedRole> possAssemblys = getMatchingAssemblys(
				seff.getBasicComponent_ServiceEffectSpecification());
		if (possAssemblys.size() > 0) {
			// conflict
			createAssemblyConflict(possAssemblys, seff);
			return null;
		}

		// new one
		return instantiateAssembly(seff.getBasicComponent_ServiceEffectSpecification());
	}

	private AssemblyContext resolveAssemblyFor(AssemblyEdge edge, RequiredRole reqRole, ProvidedRole provRole) {
		// search for matching assemblies
		List<AssemblyProvidedRole> possAssemblys = getMatchingAssemblys(provRole);
		if (possAssemblys.size() > 0) {
			// conflict
			createAssemblyConflict(possAssemblys, reqRole, edge);
			return null;
		}

		// new one
		return instantiateAssembly(provRole.getProvidingEntity_ProvidedRole());
	}

	private AssemblyContext instantiateAssembly(InterfaceProvidingEntity comp) {
		if (!(comp instanceof RepositoryComponent)) {
			return null;
		}

		AssemblyContext nAssembly = CompositionFactory.eINSTANCE.createAssemblyContext();
		nAssembly.setEntityName("Assembly" + currentAssemblyID++);
		nAssembly.setEncapsulatedComponent__AssemblyContext((RepositoryComponent) comp);
		nAssembly.setParentStructure__AssemblyContext(currentOuterStructure); // TODO
		currentOuterStructure.getAssemblyContexts__ComposedStructure().add(nAssembly);

		// add open roles
		comp.getProvidedRoles_InterfaceProvidingEntity().forEach(pr -> {
			AssemblyProvidedRole apr = new AssemblyProvidedRole();
			apr.ctx = nAssembly;
			apr.role = pr;
			openProvidedRoles.add(apr);
		});

		return nAssembly;
	}

	private void createAssemblyConflict(List<AssemblyProvidedRole> possAssemblys, RequiredRole reqRole,
			AssemblyEdge selEdge) {
		AssemblyConflict conflict = new AssemblyConflict(conflictCounter++);
		conflict.setPoss(possAssemblys.stream().map(a -> a.ctx).collect(Collectors.toList()));
		conflict.setReqRole(reqRole);
		conflict.setServiceTo(selEdge.serviceTo);

		this.currentConflict = conflict;

		assemblySelectionListener.forEach(l -> l.conflict(conflict));
	}

	private void createAssemblyConflict(List<AssemblyProvidedRole> possAssemblys, ResourceDemandingSEFF seff) {
		AssemblyConflict conflict = new AssemblyConflict(conflictCounter++);
		conflict.setPoss(possAssemblys.stream().map(a -> a.ctx).collect(Collectors.toList()));
		conflict.setReqRole(null);
		conflict.setServiceTo(seff);

		this.currentConflict = conflict;

		assemblySelectionListener.forEach(l -> l.conflict(conflict));
	}

	private List<AssemblyProvidedRole> getMatchingAssemblys(ProvidedRole provRole) {
		return openProvidedRoles.parallelStream().filter(r -> {
			boolean compMatching = r.ctx.getEncapsulatedComponent__AssemblyContext().getId()
					.equals(provRole.getProvidingEntity_ProvidedRole().getId());
			boolean roleMatching = r.role.getId().equals(provRole.getId());
			return compMatching && roleMatching;
		}).collect(Collectors.toList());
	}

	private List<AssemblyProvidedRole> getMatchingAssemblys(BasicComponent comp) {
		return openProvidedRoles.parallelStream().filter(r -> {
			return r.ctx.getEncapsulatedComponent__AssemblyContext().getId().equals(comp.getId());
		}).collect(Collectors.toList());
	}

	private void createEdgeConflict(List<AssemblyEdge> edges, RequiredRole reqRole, Signature toSig) {
		// => conflict
		ConnectionConflict conflict = new ConnectionConflict(conflictCounter++);
		conflict.setRequired(reqRole);
		conflict.setProvided(edges.parallelStream().map(e -> {
			ResourceDemandingSEFF toSeff = e.serviceTo;
			Optional<ProvidedRole> provRole = PCMUtils
					.getProvidedRoleBySignature(toSeff.getBasicComponent_ServiceEffectSpecification(), toSig);
			if (!provRole.isPresent()) {
				throw new RuntimeException(
						"Could not find provided role for a specific service call. Please check the consistency of your repository model.");
			}
			return provRole.get();
		}).collect(Collectors.toList()));

		this.currentConflict = conflict;

		connectionConflictListener.forEach(l -> l.conflict(conflict));
	}

	private void clusterAndAddOutgoingEdges(ResourceDemandingSEFF current, AssemblyContext ctx) {
		List<ServiceCallGraphEdge> edges = currentServiceCallGraph.getOutgoingEdges().get(resolveNode(current));
		Map<String, List<AssemblyEdge>> signatureClusteredEdges = new HashMap<>();

		if (edges != null) {
			edges.stream().forEach(edge -> {
				ResourceDemandingSEFF calledSeff = edge.getTo().getSeff();

				if (!signatureClusteredEdges.containsKey(calledSeff.getDescribedService__SEFF().getId())) {
					signatureClusteredEdges.put(calledSeff.getDescribedService__SEFF().getId(), Lists.newArrayList());
				}
				signatureClusteredEdges.get(calledSeff.getDescribedService__SEFF().getId())
						.add(new AssemblyEdge(current, edge.getTo().getSeff(), ctx));
			});

			// add all
			signatureClusteredEdges.values().forEach(l -> {
				currentEdges.addLast(l);
			});
		}
	}

	private ServiceCallGraphNode resolveNode(ResourceDemandingSEFF seff) {
		return currentServiceCallGraph.getNodes().stream()
				.filter(n -> n.getSeff().getId().equals(seff.getId()) && n.getHost() == null).findFirst().orElse(null);
	}

	public System getCurrentSystem() {
		return currentSystem;
	}

	public AbstractConflict<?> getCurrentConflict() {
		return currentConflict;
	}

	private class AssemblyProvidedRole {
		private AssemblyContext ctx;
		private ProvidedRole role;
	}

	private class AssemblyEdge {
		private ResourceDemandingSEFF serviceFrom;
		private ResourceDemandingSEFF serviceTo;

		private AssemblyContext assemblyFrom;

		private AssemblyEdge(ResourceDemandingSEFF serviceFrom, ResourceDemandingSEFF serviceTo,
				AssemblyContext assemblyCtxFrom) {
			this.serviceFrom = serviceFrom;
			this.serviceTo = serviceTo;
			this.assemblyFrom = assemblyCtxFrom;
		}
	}

}
