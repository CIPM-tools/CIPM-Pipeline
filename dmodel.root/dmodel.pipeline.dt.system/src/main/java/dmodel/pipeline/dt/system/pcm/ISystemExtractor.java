package dmodel.pipeline.dt.system.pcm;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.system.System;

import dmodel.pipeline.shared.structure.DirectedGraph;

public interface ISystemExtractor {

	public System buildSystemFromCallGraph(Repository repository, System initial,
			DirectedGraph<String, Integer> serviceCallGraph, IConflictResolver conflictResolver,
			IAssemblySelectionStrategy assemblySelection);

}
