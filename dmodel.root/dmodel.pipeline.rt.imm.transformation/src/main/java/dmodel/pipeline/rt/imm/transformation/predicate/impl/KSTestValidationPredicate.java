package dmodel.pipeline.rt.imm.transformation.predicate.impl;

import org.springframework.stereotype.Service;

import dmodel.pipeline.rt.imm.transformation.predicate.ValidationPredicate;
import dmodel.pipeline.rt.validation.data.ValidationMetricValue;
import dmodel.pipeline.rt.validation.data.ValidationPoint;
import dmodel.pipeline.rt.validation.data.metric.ValidationMetricType;
import dmodel.pipeline.rt.validation.data.metric.value.DoubleMetricValue;

// TODO should be configurable
@Service
public class KSTestValidationPredicate implements ValidationPredicate {
	private static final double THRES = 0.3;

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
