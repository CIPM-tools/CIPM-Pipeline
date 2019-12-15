package dmodel.pipeline.rt.validation.data.metric;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ValidationMetricType {
	KS_TEST("KS-Test");

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
