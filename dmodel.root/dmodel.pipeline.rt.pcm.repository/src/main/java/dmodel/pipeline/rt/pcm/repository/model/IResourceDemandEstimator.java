package dmodel.pipeline.rt.pcm.repository.model;

import dmodel.pipeline.rt.pcm.repository.MonitoringDataSet;

public interface IResourceDemandEstimator {

	public void prepare(MonitoringDataSet data);

	void derive();

}
