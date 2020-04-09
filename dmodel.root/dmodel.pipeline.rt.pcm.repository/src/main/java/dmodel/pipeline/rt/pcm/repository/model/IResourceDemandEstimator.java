package dmodel.pipeline.rt.pcm.repository.model;

import java.util.Map;

import dmodel.pipeline.rt.pcm.repository.MonitoringDataSet;
import dmodel.pipeline.rt.pcm.repository.RepositoryStoexChanges;

public interface IResourceDemandEstimator {
	RepositoryStoexChanges derive(Map<String, Double> currentValidationAdjustment);

	void prepare(MonitoringDataSet data);

}
