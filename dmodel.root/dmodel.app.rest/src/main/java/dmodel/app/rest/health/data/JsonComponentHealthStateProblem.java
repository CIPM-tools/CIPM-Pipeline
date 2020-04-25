package dmodel.app.rest.health.data;

import dmodel.base.core.health.HealthStateObservedComponent;
import dmodel.base.core.health.HealthStateProblemSeverity;
import lombok.Data;

@Data
public class JsonComponentHealthStateProblem {

	private HealthStateObservedComponent component;
	private HealthStateProblemSeverity severity;
	private String message;
	private long id;

}
