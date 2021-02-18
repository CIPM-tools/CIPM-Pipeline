package cipm.consistency.runtime.pipeline.pcm.repository;

import org.palladiosimulator.pcm.core.PCMRandomVariable;

import cipm.consistency.base.core.config.ModelCalibrationConfiguration;
import cipm.consistency.runtime.pipeline.pcm.repository.calibration.ExecutionTimesExtractor;

public interface IParametricRegression {

	public PCMRandomVariable performRegression(ExecutionTimesExtractor.RegressionDataset dataset,
			ModelCalibrationConfiguration config);

}
