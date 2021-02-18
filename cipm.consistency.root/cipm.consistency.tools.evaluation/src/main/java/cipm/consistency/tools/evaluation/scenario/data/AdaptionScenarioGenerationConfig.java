package cipm.consistency.tools.evaluation.scenario.data;

import cipm.consistency.tools.evaluation.scenario.data.teastore.LoadProfileType;
import cipm.consistency.tools.evaluation.scenario.data.teastore.RecommenderType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdaptionScenarioGenerationConfig {

	@Builder.Default
	private LoadProfileType initialLoad = LoadProfileType.DEFAULT_20USER;

	@Builder.Default
	private RecommenderType initialRecommender = RecommenderType.SLOPE_ONE;

	@Builder.Default
	private int scenarioCount = 30;

	@Builder.Default
	private float probMigration = 0.25f;

	@Builder.Default
	private float probReplication = 0.3f;

	@Builder.Default
	private float probUserChange = 0.3f;

	@Builder.Default
	private float probSystemChange = 0.15f;

	@Builder.Default
	private int maxReplications = 3;

	@Builder.Default
	private String referenceModelFolder = "ref_models";

}
