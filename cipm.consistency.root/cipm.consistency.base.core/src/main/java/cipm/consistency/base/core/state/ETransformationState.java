package cipm.consistency.base.core.state;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * State of a transformation within the pipeline.
 * 
 * @author David Monschein
 *
 */
public enum ETransformationState {
	/**
	 * Transformation has not been executed yet.
	 */
	NOT_REACHED("not_reached"),

	/**
	 * Transformation is currently running.
	 */
	RUNNING("running"),

	/**
	 * Transformation has finished.
	 */
	FINISHED("finished");

	/**
	 * String representation of the execution state.
	 */
	private final String name;

	/**
	 * Creates a new execution state.
	 * 
	 * @param name string representation
	 */
	private ETransformationState(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@JsonValue
	public String toString() {
		return name;
	}

}
