package dmodel.runtime.pipeline.entry.collector;

import java.util.List;

import kieker.common.record.IMonitoringRecord;

public interface IMonitoringDataCollector {

	void collect(IMonitoringRecord record);

	default void collect(List<IMonitoringRecord> records) {
		for (IMonitoringRecord record : records) {
			this.collect(record);
		}
	}

}
