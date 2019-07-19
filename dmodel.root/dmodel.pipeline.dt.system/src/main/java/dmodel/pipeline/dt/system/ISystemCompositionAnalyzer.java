package dmodel.pipeline.dt.system;

import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondence;
import dmodel.pipeline.shared.structure.DirectedGraph;
import spoon.Launcher;

public interface ISystemCompositionAnalyzer {
	public DirectedGraph<String, Integer> deriveSystemComposition(Launcher model, SpoonCorrespondence spoonCorr);
}
