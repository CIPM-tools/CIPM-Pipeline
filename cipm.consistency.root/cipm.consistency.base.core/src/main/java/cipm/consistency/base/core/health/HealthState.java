package cipm.consistency.base.core.health;

/**
 * Considered health states of a component.
 * 
 * @author David Monschein
 *
 */
public enum HealthState {
	/**
	 * No state known.
	 */
	UNKNOWN,

	/**
	 * Working flawlessly.
	 */
	WORKING,

	/**
	 * Error state, can not do its job currently.
	 */
	ERROR,

	/**
	 * Component works, but maybe not corretly.
	 */
	WARNING
}
