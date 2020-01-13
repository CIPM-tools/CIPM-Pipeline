package dmodel.pipeline.rt.validation.data.metric;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ValidationMetricType {
	KS_TEST("KS-Test"), WASSERSTEIN("Wasserstein distance"), AVG_DISTANCE_REL("Relative average distance"),
	AVG_DISTANCE_ABS("Absolute average distance");

	private String name;

	private ValidationMetricType(String name) {
		this.name = name;
	}

	@Override
	@JsonValue
	public String toString() {
		return this.name;
	}
}
