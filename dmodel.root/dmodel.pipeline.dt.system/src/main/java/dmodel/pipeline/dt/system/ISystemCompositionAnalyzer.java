package dmodel.pipeline.dt.system;

import org.palladiosimulator.pcm.repository.Repository;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondence;
import spoon.Launcher;

public interface ISystemCompositionAnalyzer {
	public ServiceCallGraph deriveSystemComposition(Launcher model, SpoonCorrespondence spoonCorr);

	public SpoonCorrespondence resolveManualMapping(Repository repository, Launcher model);
}
