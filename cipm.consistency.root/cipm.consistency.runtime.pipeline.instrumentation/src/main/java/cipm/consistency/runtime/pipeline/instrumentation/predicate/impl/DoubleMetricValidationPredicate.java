package cipm.consistency.runtime.pipeline.instrumentation.predicate.impl;

import cipm.consistency.runtime.pipeline.instrumentation.predicate.ValidationPredicate;
import cipm.consistency.runtime.pipeline.instrumentation.predicate.config.ENumericalComparator;
import cipm.consistency.runtime.pipeline.validation.data.ValidationMetricValue;
import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;
import cipm.consistency.runtime.pipeline.validation.data.metric.ValidationMetricType;
import cipm.consistency.runtime.pipeline.validation.data.metric.value.DoubleMetricValue;

public class DoubleMetricValidationPredicate implements ValidationPredicate {
	private ValidationMetricType metricType;
	private ENumericalComparator comparator;
	private double threshold;

	public DoubleMetricValidationPredicate(ValidationMetricType metric, ENumericalComparator comparator,
			double threshold) {
		this.metricType = metric;
		this.comparator = comparator;
		this.threshold = threshold;
	}

	@Override
	public boolean satisfied(ValidationPoint validationPoint) {
		for (ValidationMetricValue metric : validationPoint.getMetricValues()) {
			if (metric.type() == metricType && metric instanceof DoubleMetricValue) {
				return compare(((DoubleMetricValue) metric).getDoubleValue(), this.threshold, this.comparator);
			}
		}
		return false;
	}

	private boolean compare(double a, double b, ENumericalComparator op) {
		switch (op) {
		case EQUAL:
			return a == b;
		case GREATER:
			return a > b;
		case GREATER_EQUAL:
			return a >= b;
		case LOWER:
			return a < b;
		case LOWER_EQUAL:
			return a <= b;
		default:
			return false;
		}
	}

}
