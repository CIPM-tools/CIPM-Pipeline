package cipm.consistency.app.rest.health.data;

import cipm.consistency.base.core.health.HealthStateObservedComponent;
import cipm.consistency.base.core.health.HealthStateProblemSeverity;
import lombok.Data;

@Data
public class JsonComponentHealthStateProblem {

	private HealthStateObservedComponent component;
	private HealthStateProblemSeverity severity;
	private String message;
	private long id;

}
