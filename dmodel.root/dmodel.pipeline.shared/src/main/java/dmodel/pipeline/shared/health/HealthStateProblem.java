package dmodel.pipeline.shared.health;

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
	private HealthStateObservedComponents source;
	private String description;
	private HealthStateProblemSeverity severity;

}
