package dmodel.pipeline.rt.validation;

import dmodel.pipeline.rt.validation.contracts.IValidationListener;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.rt.validation.data.ValidationState;
import dmodel.pipeline.shared.monitoring.MonitoringDataContainer;
import dmodel.pipeline.shared.pcm.PCMInstance;

public interface IValidationProcessor {

	void input(PCMInstance instance, MonitoringDataContainer monitoringData);

	ValidationState getState();

	void registerValidationListener(IValidationListener listener);

	void unregisterValidationListener(IValidationListener listener);

	ValidationData getData();

}
