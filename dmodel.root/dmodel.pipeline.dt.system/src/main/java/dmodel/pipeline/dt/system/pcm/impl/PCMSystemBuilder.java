package dmodel.pipeline.dt.system.pcm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.system.SystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Lists;

import dmodel.pipeline.dt.system.pcm.IAssemblySelectionListener;
import dmodel.pipeline.dt.system.pcm.IConnectionConflictListener;
import dmodel.pipeline.dt.system.pcm.data.AbstractConflict;
import dmodel.pipeline.dt.system.pcm.data.AssemblyConflict;
import dmodel.pipeline.dt.system.pcm.data.ConnectionConflict;
import dmodel.pipeline.shared.pcm.PCMUtils;
import dmodel.pipeline.shared.structure.DirectedGraph;

// TODO simplify building method
public class PCMSystemBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(PCMSystemBuilder.class);

	private Repository repository;
	private System initial;

	private long conflictCounter = 0;
	private List<IConnectionConflictListener> connectionConflictListener;
	private List<IAssemblySelectionListener> assemblySelectionListener;

	// current data
	private AbstractConflict<?> currentConflict;
	private List<AssemblyProvidedRole> openProvidedRoles;
	private System currentSystem;
	private Iterator<String> entryPoints;
	private LinkedList<List<Pair<String, String>>> currentEdges; // clustered over signature

	private DirectedGraph<String, Integer> currentServiceCallGraph;

	public PCMSystemBuilder(Repository repository, System initial) {
		this.repository = repository;
		this.initial = initial;

		this.assemblySelectionListener = new ArrayList<>();
		this.connectionConflictListener = new ArrayList<>();
	}

	public boolean startBuildingSystem(DirectedGraph<String, Integer> serviceCallGraph) {
		// 0. create output system
		currentSystem = SystemFactory.eINSTANCE.createSystem();

		// 1.1. find entry points to the call graph
		entryPoints = serviceCallGraph.getNodes().stream().filter(n -> serviceCallGraph.incomingEdges(n) == 0)
				.collect(Collectors.toCollection(LinkedHashSet::new)).iterator();

		// 1.2. containers
		openProvidedRoles = Lists.newArrayList();
		currentEdges = Lists.newLinkedList();

		// 1.3. default values
		currentConflict = null;
		currentServiceCallGraph = serviceCallGraph;

		// 2. try to build a system and corresponding conflicts that need to be
		// resolved
		return buildingStep();
	}

	public void continueBuilding() {
		if (currentConflict != null && currentConflict.isSolved()) {
			// TODO
		} else {
			throw new IllegalStateException("Cannot continue if there is no resolved conflict.");
		}
	}

	private boolean buildingStep() {
		// if there is a conflict we cannot do anything
		if (currentConflict != null) {
			return false;
		}

		if (currentEdges.isEmpty()) {
			// we search for an entry point
			if (!entryPoints.hasNext()) {
				// we are finished
				return true;
			} else {
				// pop an entry point and get all edges
				String currentEp = entryPoints.next();
				List<Pair<String, Integer>> edges = currentServiceCallGraph.getOutgoingEdges(currentEp);
				if (edges != null && edges.size() > 0) {
					// collect them and put to the list
					Map<String, List<Pair<String, String>>> signatureClusteredEdges = new HashMap<>();
					edges.parallelStream().forEach(edge -> {
						ResourceDemandingSEFF calledSeff = PCMUtils.getElementById(repository,
								ResourceDemandingSEFF.class, edge.getLeft());
						if (signatureClusteredEdges.containsKey(calledSeff.getDescribedService__SEFF().getId())) {
							signatureClusteredEdges.put(calledSeff.getDescribedService__SEFF().getId(),
									Lists.newArrayList());
						}
						signatureClusteredEdges.get(calledSeff.getDescribedService__SEFF().getId())
								.add(Pair.of(currentEp, edge.getLeft()));
					});

					// add all
					signatureClusteredEdges.values().forEach(l -> {
						currentEdges.addLast(l);
					});
				}

				// recursion -> next step
				return buildingStep();
			}
		} else {
			// pop a edge and process it
			List<Pair<String, String>> edges = currentEdges.removeFirst();
			if (edges.size() > 0) {
				// resolve corresponding seffs
				ResourceDemandingSEFF from = PCMUtils.getElementById(repository, ResourceDemandingSEFF.class,
						edges.get(0).getLeft()); // always the same
				Signature toSig = PCMUtils
						.getElementById(repository, ResourceDemandingSEFF.class, edges.get(0).getRight())
						.getDescribedService__SEFF();

				// get components for this seff
				BasicComponent cfrom = from.getBasicComponent_ServiceEffectSpecification();

				// get the required role
				Optional<RequiredRole> reqRole = PCMUtils.getRequiredRoleBySignature(cfrom, toSig);

				if (!reqRole.isPresent()) {
					throw new RuntimeException(
							"Could not find required role for a specific service call. Please check the consistency of your repository model.");
				}

				// edges > 1?
				if (edges.size() > 1) {
					// => conflict
					ConnectionConflict conflict = new ConnectionConflict(conflictCounter++);
					conflict.setRequired(reqRole.get());
					conflict.setProvided(edges.parallelStream().map(e -> {
						ResourceDemandingSEFF toSeff = PCMUtils.getElementById(repository, ResourceDemandingSEFF.class,
								e.getRight());
						Optional<ProvidedRole> provRole = PCMUtils.getProvidedRoleBySignature(
								toSeff.getBasicComponent_ServiceEffectSpecification(), toSig);
						if (!provRole.isPresent()) {
							throw new RuntimeException(
									"Could not find provided role for a specific service call. Please check the consistency of your repository model.");
						}
						return provRole.get();
					}).collect(Collectors.toList()));
					connectionConflictListener.forEach(l -> l.conflict(conflict));
					return false;
				}
				ResourceDemandingSEFF linkTo = PCMUtils.getElementById(repository, ResourceDemandingSEFF.class,
						edges.get(0).getRight());
				Optional<ProvidedRole> provRole = PCMUtils
						.getProvidedRoleBySignature(linkTo.getBasicComponent_ServiceEffectSpecification(), toSig);

				// search for matching assemblies
				List<AssemblyProvidedRole> possAssemblys = openProvidedRoles.parallelStream().filter(r -> {
					boolean compMatching = r.ctx.getEncapsulatedComponent__AssemblyContext().getId()
							.equals(linkTo.getBasicComponent_ServiceEffectSpecification().getId());
					boolean roleMatching = r.role.getId().equals(provRole.get().getId());
					return compMatching && roleMatching;
				}).collect(Collectors.toList());
				if (possAssemblys.size() > 0) {
					// conflict
					AssemblyConflict conflict = new AssemblyConflict(conflictCounter++);
					conflict.setPoss(possAssemblys.stream().map(a -> a.ctx).collect(Collectors.toList()));
					conflict.setReqRole(reqRole.get());

					assemblySelectionListener.forEach(l -> l.conflict(conflict));
					return false;
				}

				// new one
				AssemblyContext nAssembly = CompositionFactory.eINSTANCE.createAssemblyContext();
				nAssembly.setEncapsulatedComponent__AssemblyContext(
						linkTo.getBasicComponent_ServiceEffectSpecification());
				nAssembly.setParentStructure__AssemblyContext(currentSystem); // TODO

				// add to the container
				currentSystem.getAssemblyContexts__ComposedStructure().add(nAssembly);

				// connect it
				// TODO

				// add outgoing edges from the to node
				// TODO
			} else {
				return buildingStep();
			}
		}

		return true;
	}

	public System getCurrentSystem() {
		return currentSystem;
	}

	private class AssemblyProvidedRole {
		private AssemblyContext ctx;
		private ProvidedRole role;
	}

}
