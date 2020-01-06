package dmodel.pipeline.rt.validation;

import java.util.List;

import dmodel.pipeline.models.mapping.PalladioRuntimeMapping;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

public interface IValidationProcessor {

	ValidationData process(InMemoryPCM instance, PalladioRuntimeMapping mapping, List<PCMContextRecord> monitoringData,
			String taskName);

}
