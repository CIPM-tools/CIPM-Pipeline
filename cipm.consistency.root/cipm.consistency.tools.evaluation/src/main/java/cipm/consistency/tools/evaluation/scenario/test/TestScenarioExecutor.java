package cipm.consistency.tools.evaluation.scenario.test;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import cipm.consistency.tools.evaluation.scenario.AdaptionScenarioOrchestrator;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioExecutionConfig;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioList;

public class TestScenarioExecutor {

	public static void main(String[] args) {
		File testScenarioInput = new File("scenarios/demo.asf");
		ObjectMapper mapper = new ObjectMapper();
		try {
			AdaptionScenarioList scenario = mapper.readValue(testScenarioInput, AdaptionScenarioList.class);
			new AdaptionScenarioOrchestrator().applyScenario(scenario, new AdaptionScenarioExecutionConfig());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
