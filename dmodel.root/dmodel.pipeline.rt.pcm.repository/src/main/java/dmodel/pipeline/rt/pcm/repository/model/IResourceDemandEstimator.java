package dmodel.pipeline.rt.pcm.repository.model;

import java.util.Map;

import dmodel.pipeline.rt.pcm.repository.MonitoringDataSet;

public interface IResourceDemandEstimator {
	void derive(Map<String, Double> currentValidationAdjustment);

	void prepare(MonitoringDataSet data);

}
