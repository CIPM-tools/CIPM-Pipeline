package cipm.consistency.tools.evaluation.scenario.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.MigrationScenario;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.ReplicationScenario;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.SystemChangeScenario;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.UserBehaviorChangeScenario;
import lombok.Data;

@Data
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "scenario_type")
@JsonSubTypes({ @JsonSubTypes.Type(value = ReplicationScenario.class, name = "replication"),
		@JsonSubTypes.Type(value = UserBehaviorChangeScenario.class, name = "workload"),
		@JsonSubTypes.Type(value = SystemChangeScenario.class, name = "system"),
		@JsonSubTypes.Type(value = MigrationScenario.class, name = "migration") })
public abstract class AdaptionScenario {

	private AdaptionScenarioType type;

	public AdaptionScenario() {
	}

	protected AdaptionScenario(AdaptionScenarioType type) {
		this.type = type;
	}

	public abstract void execute(AdaptionScenarioExecutionConfig config);

	public abstract InMemoryPCM generateReferenceModel(InMemoryPCM current);

}
