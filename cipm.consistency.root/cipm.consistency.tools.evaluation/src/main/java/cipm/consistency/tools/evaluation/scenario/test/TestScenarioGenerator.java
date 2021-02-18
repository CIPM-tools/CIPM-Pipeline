package cipm.consistency.tools.evaluation.scenario.test;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioList;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.ReplicationScenario;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.SystemChangeScenario;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.UserBehaviorChangeScenario;
import cipm.consistency.tools.evaluation.scenario.data.teastore.LoadProfileType;
import cipm.consistency.tools.evaluation.scenario.data.teastore.RecommenderType;
import cipm.consistency.tools.evaluation.scenario.data.teastore.ReplicationComponentType;

public class TestScenarioGenerator {

	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();

		AdaptionScenarioList output = new AdaptionScenarioList();
		output.getInitialScenarios().add(new UserBehaviorChangeScenario(LoadProfileType.DEFAULT_20USER));
		output.getScenarios().add(new ReplicationScenario(ReplicationComponentType.AUTH, 2));
		output.getScenarios().add(new SystemChangeScenario(RecommenderType.DUMMY));

		try {
			mapper.writeValue(new File("scenarios/demo.asf"), output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
