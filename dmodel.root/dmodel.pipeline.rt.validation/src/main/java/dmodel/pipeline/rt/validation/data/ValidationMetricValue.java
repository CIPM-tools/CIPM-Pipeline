package dmodel.pipeline.rt.validation.data;

public abstract class ValidationMetricValue {

	public abstract Object value();

	public abstract double compare(Object other);

	public abstract ValidationMetricType type();

}
