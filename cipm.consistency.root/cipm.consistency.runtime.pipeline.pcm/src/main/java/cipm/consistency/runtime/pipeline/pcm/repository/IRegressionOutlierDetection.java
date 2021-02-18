package cipm.consistency.runtime.pipeline.pcm.repository;

import cipm.consistency.runtime.pipeline.pcm.repository.calibration.ExecutionTimesExtractor.RegressionDataset;

public interface IRegressionOutlierDetection {
	public void filterOutliers(RegressionDataset dataset);

	public double[] filterOutliers(double[] data);
}
