package dmodel.pipeline.rt.validation;

import java.util.List;

import dmodel.pipeline.monitoring.records.ServiceContextRecord;
import dmodel.pipeline.rt.validation.data.ValidationMetric;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

public interface IValidationProcessor {

	List<ValidationMetric> process(InMemoryPCM instance, List<ServiceContextRecord> monitoringData, String taskName);

}
