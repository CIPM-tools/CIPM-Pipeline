package dmodel.base.core.health;

/**
 * Severity types for a problem (see {@link HealthStateProblem}).
 * 
 * @author David Monschein
 *
 */
public enum HealthStateProblemSeverity {
	/**
	 * Just an information. Has no influence on the functionality.
	 */
	INFO,

	/**
	 * Functionality is restricted or does not work as intended.
	 */
	WARNING,

	/**
	 * Functionality discontinued due to an error.
	 */
	ERROR
}
