package dmodel.pipeline.dt.system;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondence;
import spoon.Launcher;

public interface ISystemCompositionAnalyzer {
	public ServiceCallGraph deriveSystemComposition(Launcher model, SpoonCorrespondence spoonCorr);
}
