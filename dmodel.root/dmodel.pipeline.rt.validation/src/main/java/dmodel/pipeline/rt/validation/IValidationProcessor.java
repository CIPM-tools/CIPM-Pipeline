package dmodel.pipeline.rt.validation;

import java.util.List;

import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

public interface IValidationProcessor {

	ValidationData process(InMemoryPCM instance, List<PCMContextRecord> monitoringData, String taskName);

	void clearSimulationData();

}
