package cipm.consistency.tools.evaluation.scenario.data;

import lombok.Data;

@Data
public class AdaptionScenarioExecutionConfig {

	private String scenariosPath;
	private String loadOrchestratorRestURL;
	private String teastoreRestURL;
	private String pipelineStatusRestURL;

	private int secondsBetweenScenarios = 60 * 5; // 5 minutes

	// start conditions
	private int secondsTillStart = 60 * 3; // 3 minutes
	private String checkReachableUrl;

}
