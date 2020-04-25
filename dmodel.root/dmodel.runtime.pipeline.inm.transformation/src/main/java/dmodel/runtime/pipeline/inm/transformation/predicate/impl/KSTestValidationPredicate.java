package dmodel.runtime.pipeline.inm.transformation.predicate.impl;

import org.springframework.stereotype.Service;

import dmodel.runtime.pipeline.inm.transformation.predicate.ValidationPredicate;
import dmodel.runtime.pipeline.validation.data.ValidationMetricValue;
import dmodel.runtime.pipeline.validation.data.ValidationPoint;
import dmodel.runtime.pipeline.validation.data.metric.ValidationMetricType;
import dmodel.runtime.pipeline.validation.data.metric.value.DoubleMetricValue;

// TODO should be configurable
@Service
public class KSTestValidationPredicate implements ValidationPredicate {
	private static final double THRES = 0.2;

	@Override
	public boolean satisfied(ValidationPoint validationPoint) {
		for (ValidationMetricValue metric : validationPoint.getMetricValues()) {
			if (metric.type() == ValidationMetricType.KS_TEST) {
				return (double) ((DoubleMetricValue) metric).value() <= THRES;
			}
		}
		return false;
	}

}
