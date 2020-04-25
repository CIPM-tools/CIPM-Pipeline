package dmodel.runtime.pipeline.validation;

import java.util.List;

import dmodel.base.shared.pcm.InMemoryPCM;
import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.runtime.pipeline.validation.data.ValidationData;

public interface IValidationProcessor {

	ValidationData process(InMemoryPCM instance, List<PCMContextRecord> monitoringData, String taskName);

	void clearSimulationData();

}
