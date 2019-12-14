package dmodel.pipeline.rt.validation.data.metric;

import dmodel.pipeline.rt.validation.data.ValidationMetricValue;
import dmodel.pipeline.rt.validation.data.ValidationPoint;

public interface IValidationMetric<T extends ValidationMetricValue> {

	public boolean isTarget(ValidationPoint point);

	public T calculate(ValidationPoint point);

}
