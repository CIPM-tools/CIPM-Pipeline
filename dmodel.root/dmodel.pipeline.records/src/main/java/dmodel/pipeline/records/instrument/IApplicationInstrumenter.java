package dmodel.pipeline.records.instrument;

import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondence;
import spoon.Launcher;

public interface IApplicationInstrumenter {

	public boolean instrumentApplication(Launcher model, InstrumentationMetadata metadata,
			SpoonCorrespondence spoonCorr);

	public Launcher prepareModifiableModel(ApplicationProject project, ApplicationProject agentConfig,
			String outputPath);

}
