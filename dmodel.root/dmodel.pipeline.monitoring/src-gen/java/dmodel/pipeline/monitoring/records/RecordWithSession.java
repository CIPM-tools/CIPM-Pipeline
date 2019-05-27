package dmodel.pipeline.monitoring.records;

import kieker.common.record.IMonitoringRecord;

/**
 * @author Generic Kieker
 * 
 * @since 1.13
 */
public interface RecordWithSession extends IMonitoringRecord {
	public String getSessionId();
	
}
