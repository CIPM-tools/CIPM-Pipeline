package dmodel.pipeline.dt.system.pcm.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.system.SystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Lists;

import dmodel.pipeline.dt.system.pcm.IAssemblySelectionStrategy;
import dmodel.pipeline.dt.system.pcm.IConflictResolver;
import dmodel.pipeline.dt.system.pcm.ISystemExtractor;
import dmodel.pipeline.shared.PCMUtils;
import dmodel.pipeline.shared.structure.DirectedGraph;

public class PCMSystemBuilder implements ISystemExtractor {
	private static final Logger LOG = LoggerFactory.getLogger(PCMSystemBuilder.class);

	@Override
	public System buildSystemFromCallGraph(Repository repository, System initial,
			DirectedGraph<String, Integer> serviceCallGraph, IConflictResolver conflictResolver,
			IAssemblySelectionStrategy assemblySelection) {
		LOG.info("Starting system derivation process (DT).");
		// 0. create output system
		System enclosingSystem = SystemFactory.eINSTANCE.createSystem();
		
		// 1.1. find entry points to the call graph
		Set<String> entryPoints = serviceCallGraph.getNodes().stream()
				.filter(n -> serviceCallGraph.incomingEdges(n) == 0).collect(Collectors.toSet());

		// 2. try to build a system and corresponding conflicts that need to be
		// resolved
		List<ProvidedRole> unlinkedRoles = Lists.newArrayList();
		List<Pair<RequiredRole, List<ProvidedRole>>> conflicts = Lists.newArrayList();
		for (String entrySeff : entryPoints) {
			// here we start with linking
			ResourceDemandingSEFF seff = PCMUtils.getElementById(repository, ResourceDemandingSEFF.class, entrySeff);
			BasicComponent component = seff.getBasicComponent_ServiceEffectSpecification();
		}

		// 3. use the old system and find projection to resolve conflicts
		// TODO

		// 4. resolve still existing conflicts
		// TODO

		// 5. return built system
		// TODO

		return null;
	}

}
