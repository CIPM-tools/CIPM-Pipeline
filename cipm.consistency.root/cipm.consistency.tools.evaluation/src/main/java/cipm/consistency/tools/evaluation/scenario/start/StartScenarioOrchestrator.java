package cipm.consistency.tools.evaluation.scenario.start;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

import cipm.consistency.tools.evaluation.scenario.AdaptionScenarioOrchestrator;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioExecutionConfig;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioList;
import lombok.extern.java.Log;

@Log
public class StartScenarioOrchestrator {

	public static void main(String[] args) {
		String scenarioFile = null;
		String configFile = null;
		Pattern argumentPattern = Pattern.compile("\\-\\-(.*?)\\=(.*?)\\z");

		for (String arg : args) {
			Matcher argumentMatcher = argumentPattern.matcher(arg);
			if (argumentMatcher.find()) {
				String key = argumentMatcher.group(1);
				String value = argumentMatcher.group(2);

				if (key.equals("scenarios")) {
					scenarioFile = value;
				} else if (key.equals("config")) {
					configFile = value;
				}
			}
		}

		if (scenarioFile != null && configFile != null) {
			triggerScenarioExecution(scenarioFile, configFile);
		}
	}

	private static void triggerScenarioExecution(String scenarioFile, String configFile) {
		log.info("Starting scenario orchestrator.");
		log.info("Scenario file: " + scenarioFile);
		log.info("Configuration file: " + configFile);
		ObjectMapper mapper = new ObjectMapper();

		try {
			AdaptionScenarioList wholeScenario = mapper.readValue(new File(scenarioFile), AdaptionScenarioList.class);
			AdaptionScenarioExecutionConfig configRead = mapper.readValue(new File(configFile),
					AdaptionScenarioExecutionConfig.class);

			AdaptionScenarioOrchestrator orch = new AdaptionScenarioOrchestrator();
			orch.applyScenario(wholeScenario, configRead);
		} catch (IOException e) {
			e.printStackTrace();
			log.warning("Failed to load scenario and/or configuration file.");
		}
	}

}
