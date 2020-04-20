package dmodel.pipeline.rt.rest.health.data;

import dmodel.pipeline.core.health.HealthStateObservedComponent;
import dmodel.pipeline.core.health.HealthStateProblemSeverity;
import lombok.Data;

@Data
public class JsonComponentHealthStateProblem {

	private HealthStateObservedComponent component;
	private HealthStateProblemSeverity severity;
	private String message;
	private long id;

}
