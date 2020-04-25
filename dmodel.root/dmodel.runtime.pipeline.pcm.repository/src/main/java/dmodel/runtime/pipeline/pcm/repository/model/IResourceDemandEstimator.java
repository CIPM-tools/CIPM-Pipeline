package dmodel.runtime.pipeline.pcm.repository.model;

import java.util.Map;

import dmodel.runtime.pipeline.pcm.repository.MonitoringDataSet;
import dmodel.runtime.pipeline.pcm.repository.RepositoryStoexChanges;

public interface IResourceDemandEstimator {
	RepositoryStoexChanges derive(Map<String, Double> currentValidationAdjustment);

	void prepare(MonitoringDataSet data);

}
