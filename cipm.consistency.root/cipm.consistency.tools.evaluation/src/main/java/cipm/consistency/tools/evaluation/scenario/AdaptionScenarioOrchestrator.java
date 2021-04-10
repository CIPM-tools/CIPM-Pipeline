package cipm.consistency.tools.evaluation.scenario;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenario;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioExecutionConfig;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioList;
import cipm.consistency.tools.evaluation.scenario.data.pipeline.PipelineUIState;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.UserBehaviorChangeScenario;
import cipm.consistency.tools.evaluation.scenario.data.teastore.LoadProfileType;
import cipm.consistency.tools.evaluation.scenario.helper.DefaultHttpClient;
import lombok.extern.java.Log;

@Log
public class AdaptionScenarioOrchestrator {
	private ScheduledExecutorService scenarioExecutionService;
	private DefaultHttpClient http;
	private ObjectMapper objectMapper;

	private LoadProfileType currentLoadProfile;
	private LoadProfileType initialLoadProfile;

	public AdaptionScenarioOrchestrator() {
		this.scenarioExecutionService = Executors.newSingleThreadScheduledExecutor();
		this.http = new DefaultHttpClient(2000);
		this.objectMapper = new ObjectMapper();
	}

	public void applyScenario(AdaptionScenarioList list, AdaptionScenarioExecutionConfig config) {
		log.info("Initial startup for applying scenario.");
		scenarioExecutionService.schedule(() -> startScenario(list, config), config.getSecondsTillStart(),
				TimeUnit.SECONDS);
	}

	private void startScenario(AdaptionScenarioList list, AdaptionScenarioExecutionConfig config) {
		log.info("Starting the scenario after initial waiting interval.");
		// wait until url available
		if (http.isReachable(config.getCheckReachableUrl())) {
			syncWithPipelineExecution(list, config);
		} else {
			scenarioExecutionService.schedule(() -> startScenario(list, config), 5000, TimeUnit.MILLISECONDS);
		}
	}

	private void syncWithPipelineExecution(AdaptionScenarioList list, AdaptionScenarioExecutionConfig config) {
		log.info("Waiting for pipeline execution due to synchronization purposes.");
		String pipelineStatusUrl = config.getPipelineStatusRestURL();
		if (http.isReachable(pipelineStatusUrl)) {
			String pipelineStatus = http.getRequest(pipelineStatusUrl, Maps.newHashMap());
			try {
				PipelineUIState pipelineState = objectMapper.readValue(pipelineStatus, PipelineUIState.class);
				if (pipelineState.isRunning()) {
					triggerScenario(list, config);
				} else {
					scenarioExecutionService.schedule(() -> syncWithPipelineExecution(list, config), 1000,
							TimeUnit.MILLISECONDS);
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} else {
			scenarioExecutionService.schedule(() -> syncWithPipelineExecution(list, config), 1000,
					TimeUnit.MILLISECONDS);
		}
	}

	private void triggerScenario(AdaptionScenarioList list, AdaptionScenarioExecutionConfig config) {
		log.info("URLs are all reachable. Starting with execution of initial scenarios.");
		// save that timestamp
		try {
			FileUtils.writeLines(new File("offset.txt"),
					Lists.newArrayList(String.valueOf(System.currentTimeMillis())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// execute initial scenarios
		list.getInitialScenarios().forEach(initial -> {
			if (initial instanceof UserBehaviorChangeScenario) {
				this.currentLoadProfile = ((UserBehaviorChangeScenario) initial).getLoadType();
				this.initialLoadProfile = ((UserBehaviorChangeScenario) initial).getLoadType();
			}

			initial.execute(config);
		});

		log.info("Scheduling all scenarios and wait for their execution.");
		// start executing all others
		scenarioExecutionService.scheduleAtFixedRate(() -> executeSingleScenario(list, config),
				Math.round(config.getSecondsBetweenScenarios() * 0.9f), config.getSecondsBetweenScenarios(),
				TimeUnit.SECONDS);
	}

	private void executeSingleScenario(AdaptionScenarioList list, AdaptionScenarioExecutionConfig config) {
		// wait until pipeline is running
		String pipelineStatusUrl = config.getPipelineStatusRestURL();
		if (http.isReachable(pipelineStatusUrl)) {
			String pipelineStatus = http.getRequest(pipelineStatusUrl, Maps.newHashMap());
			try {
				PipelineUIState pipelineState = objectMapper.readValue(pipelineStatus, PipelineUIState.class);
				if (pipelineState.isRunning()) {
					scenarioExecutionService.schedule(() -> executeSingleScenarioNow(list, config), 1000,
							TimeUnit.MILLISECONDS);
				} else {
					scenarioExecutionService.schedule(() -> executeSingleScenario(list, config), 250,
							TimeUnit.MILLISECONDS);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void executeSingleScenarioNow(AdaptionScenarioList list, AdaptionScenarioExecutionConfig config) {
		if (list.getScenarios().size() > 0) {
			AdaptionScenario scen = list.getScenarios().get(0);
			log.info("Executing scenario (" + scen.getType() + ").");

			if (scen instanceof UserBehaviorChangeScenario) {
				this.currentLoadProfile = ((UserBehaviorChangeScenario) scen).getLoadType();
			} else {
				this.currentLoadProfile = this.initialLoadProfile;
			}

			// stop load before
			new UserBehaviorChangeScenario(LoadProfileType.NONE).execute(config);
			try {
				scen.execute(config);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				list.getScenarios().remove(0);
				// restore load
				new UserBehaviorChangeScenario(currentLoadProfile).execute(config);
			}
		} else {
			// terminate application
			System.exit(0);
		}
	}

}
