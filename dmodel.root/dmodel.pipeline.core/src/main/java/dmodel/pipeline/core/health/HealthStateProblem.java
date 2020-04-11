package dmodel.pipeline.core.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HealthStateProblem {

	private long id;
	private HealthStateObservedComponent source;
	private String description;
	private HealthStateProblemSeverity severity;

}
