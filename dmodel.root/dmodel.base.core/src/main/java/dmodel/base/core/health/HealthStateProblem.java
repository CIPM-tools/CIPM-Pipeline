package dmodel.base.core.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Structure for a health state problem (see {@link HealthStateManager} and
 * {@link AbstractHealthStateComponent}).
 * 
 * @author David Monschein
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HealthStateProblem {

	/**
	 * ID of the problem.
	 */
	private long id;

	/**
	 * Source of the problem.
	 */
	private HealthStateObservedComponent source;

	/**
	 * Description of the problem.
	 */
	private String description;

	/**
	 * Severity of the problem.
	 */
	private HealthStateProblemSeverity severity;

}
