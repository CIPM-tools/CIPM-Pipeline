package dmodel.pipeline.rt.validation.data.metric.impl;

import org.pcm.headless.shared.data.results.MeasuringPointType;

import dmodel.pipeline.rt.validation.data.ValidationPoint;
import dmodel.pipeline.rt.validation.data.metric.IValidationMetric;
import dmodel.pipeline.rt.validation.data.metric.value.DoubleMetricValue;

public abstract class ServiceCallMetric implements IValidationMetric<DoubleMetricValue> {

	@Override
	public boolean isTarget(ValidationPoint point) {
		if (point.getMeasuringPoint().getType() == MeasuringPointType.ASSEMBLY_OPERATION
				|| point.getMeasuringPoint().getType() == MeasuringPointType.ENTRY_LEVEL_CALL) {
			return true;
		}
		return false;
	}

}
