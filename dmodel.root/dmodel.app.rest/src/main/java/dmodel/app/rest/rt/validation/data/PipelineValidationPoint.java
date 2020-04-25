package dmodel.app.rest.rt.validation.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PipelineValidationPoint {
	PRE("pre"), AFTER_USAGE("afterusage"), AFTER_REPO("afterrepo"), FINAL("final");

	private String name;

	private PipelineValidationPoint(String name) {
		this.name = name;
	}

	@Override
	@JsonValue
	public String toString() {
		return this.name;
	}

	public static PipelineValidationPoint fromString(String text) {
		for (PipelineValidationPoint b : PipelineValidationPoint.values()) {
			if (b.name.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}

}
