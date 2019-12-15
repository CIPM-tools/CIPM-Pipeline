package dmodel.pipeline.rt.validation.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import dmodel.pipeline.rt.validation.data.metric.ValidationMetricType;

public abstract class ValidationMetricValue {

	@JsonProperty
	public abstract Object value();

	public abstract double compare(Object other);

	@JsonProperty
	public abstract ValidationMetricType type();

}
