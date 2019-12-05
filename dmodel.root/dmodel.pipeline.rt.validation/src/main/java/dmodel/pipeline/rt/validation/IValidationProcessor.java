package dmodel.pipeline.rt.validation;

import dmodel.pipeline.shared.monitoring.MonitoringDataContainer;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

public interface IValidationProcessor {

	void process(InMemoryPCM instance, MonitoringDataContainer monitoringData);

}
