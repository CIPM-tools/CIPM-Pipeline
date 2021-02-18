package cipm.consistency.base.core.state;

import lombok.Getter;

/**
 * Validation points within the pipeline.
 * 
 * @author David Monschein
 *
 */
public enum ValidationSchedulePoint {
	/**
	 * Validation which is executed before all other transformations in the
	 * pipeline.
	 */
	PRE_PIPELINE("Pipeline Pre-Validation"),

	/**
	 * Validation after updating the usage model.
	 */
	AFTER_T_USAGE("Pipeline Intermediate-Validation 1"),

	/**
	 * Validation after updating the repository model.
	 */
	AFTER_T_REPO("Pipeline Intermediate-Validation 2"),

	/**
	 * Validation at the end of the pipeline.
	 */
	FINAL("Pipeline Final-Validation"),

	/**
	 * Extraordinary validation which can be triggered. (not used yet)
	 */
	EXTRAORDINARY("Pipeline Extraordinary-Validation");

	/**
	 * String representation of the validation point.
	 */
	@Getter
	private String name;

	/**
	 * Creates a new validation point.
	 * 
	 * @param name name of the validation point
	 */
	private ValidationSchedulePoint(String name) {
		this.name = name;
	}

}
