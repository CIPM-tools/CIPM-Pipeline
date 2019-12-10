package dmodel.pipeline.rt.validation.data.impl;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.primitives.Doubles;

import dmodel.pipeline.rt.validation.data.DistributionMetric;
import dmodel.pipeline.rt.validation.data.ValidationMetricType;

public class KSTestMetric extends DistributionMetric {
	// >0 if this object is better, < 0 if the other one is better
	@Override
	public double compare(Object other) {
		if (other instanceof KSTestMetric) {
			return ((KSTestMetric) other).value() - value();
		}
		return 0;
	}

	@JsonValue
	public double value() {
		return new KolmogorovSmirnovTest().kolmogorovSmirnovTest(Doubles.toArray(valuesA), Doubles.toArray(valuesB));
	}

	@Override
	public ValidationMetricType type() {
		return ValidationMetricType.KS_TEST;
	}

}
