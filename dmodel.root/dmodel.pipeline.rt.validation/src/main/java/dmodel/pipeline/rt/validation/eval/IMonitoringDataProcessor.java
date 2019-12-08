package dmodel.pipeline.rt.validation.eval;

import dmodel.pipeline.monitoring.records.ServiceContextRecord;

public interface IMonitoringDataProcessor extends IValidationDataGenerator {

	public void processMonitoringRecord(ServiceContextRecord record);

}
