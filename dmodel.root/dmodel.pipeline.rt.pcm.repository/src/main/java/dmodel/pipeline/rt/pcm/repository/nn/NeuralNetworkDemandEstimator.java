package dmodel.pipeline.rt.pcm.repository.nn;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dmodel.pipeline.monitoring.records.ResponseTimeRecord;
import dmodel.pipeline.rt.pcm.repository.MonitoringDataSet;
import dmodel.pipeline.rt.pcm.repository.model.IResourceDemandEstimator;

public class NeuralNetworkDemandEstimator implements IResourceDemandEstimator {

	@Override
	public void prepare(MonitoringDataSet data) {
		// 1. for each action id own set
		Map<String, List<ResponseTimeRecord>> responseTimeBuckets = Maps.newHashMap();
		data.getRaw().stream().filter(f -> f instanceof ResponseTimeRecord).map(ResponseTimeRecord.class::cast)
				.forEach(rsp -> {
					if (!responseTimeBuckets.containsKey(rsp.getInternalActionId())) {
						responseTimeBuckets.put(rsp.getInternalActionId(), Lists.newArrayList(rsp));
					} else {
						responseTimeBuckets.get(rsp.getInternalActionId()).add(rsp);
					}
				});

		// 2. for each bucket create a network
		responseTimeBuckets.entrySet().stream().forEach(bucket -> {

		});
	}

	@Override
	public void derive() {
		// TODO Auto-generated method stub

	}

}
