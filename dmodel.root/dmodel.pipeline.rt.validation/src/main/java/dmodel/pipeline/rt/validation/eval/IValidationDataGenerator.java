package dmodel.pipeline.rt.validation.eval;

import java.util.List;

import dmodel.pipeline.rt.validation.data.ValidationMetric;

public interface IValidationDataGenerator {

	public List<ValidationMetric> getValidationMetrics();

	public void clearValidationMetrics();

}
