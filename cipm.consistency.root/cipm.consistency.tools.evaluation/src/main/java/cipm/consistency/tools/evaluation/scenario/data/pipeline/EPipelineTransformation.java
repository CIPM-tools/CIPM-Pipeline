package cipm.consistency.tools.evaluation.scenario.data.pipeline;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * All types of transformations that are part of the pipeline.
 * 
 * @author David Monschein
 *
 */
public enum EPipelineTransformation {
	/**
	 * Validation step that is executed as first step within the pipeline.
	 */
	PRE_VALIDATION("pre-validation"),

	/**
	 * Derivation of updates in the resource environment.
	 */
	T_RESOURCEENV("t-resourceenv"),

	/**
	 * Derivation of updates in the system model and in the allocation model.
	 */
	T_SYSTEM("t-system"),

	/**
	 * Validation step after the first repository transformation.
	 */
	T_VALIDATION21("t-validation2-1"),

	/**
	 * Validation step after the first usage model transformation.
	 */
	T_VALIDATION22("t-validation2-2"),

	/**
	 * First transformation of the usage model.
	 */
	T_USAGEMODEL1("t-usagemodel1"),

	/**
	 * First transformation of the repository.
	 */
	T_REPOSITORY1("t-repository1"),

	/**
	 * The second transformation of the usage model, executed because the first
	 * repository model transformation was more accurate than the first usage model
	 * transformation.
	 */
	T_USAGEMODEL2("t-usagemodel2"),

	/**
	 * The second transformation of the repository model, executed because the first
	 * usage model transformation was more accurate than the first repository model
	 * transformation.
	 */
	T_REPOSITORY2("t-repository2"),

	/**
	 * The final validation step within the pipeline.
	 */
	T_VALIDATION3("t-validation3");

	/**
	 * Name of the transformation.
	 */
	private final String name;

	/**
	 * Creates a new transformation type
	 * 
	 * @param name name of the transformation
	 */
	private EPipelineTransformation(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the transformation.
	 */
	@Override
	@JsonValue
	public String toString() {
		return name;
	}

}
