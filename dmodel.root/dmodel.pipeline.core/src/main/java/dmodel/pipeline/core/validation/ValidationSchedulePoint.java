package dmodel.pipeline.core.validation;

import lombok.Getter;

public enum ValidationSchedulePoint {
	PRE_PIPELINE("Pipeline Pre-Validation"), AFTER_T_USAGE("Pipeline Intermediate-Validation 1"),
	AFTER_T_REPO("Pipeline Intermediate-Validation 2"), FINAL("Pipeline Final-Validation"),
	EXTRAORDINARY("Pipeline Extraordinary-Validation");

	@Getter
	private String name;

	private ValidationSchedulePoint(String name) {
		this.name = name;
	}

}
