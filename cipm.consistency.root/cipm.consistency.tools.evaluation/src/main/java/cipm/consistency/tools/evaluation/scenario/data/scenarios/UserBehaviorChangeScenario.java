package cipm.consistency.tools.evaluation.scenario.data.scenarios;

import java.util.Map;

import com.google.common.collect.Maps;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenario;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioExecutionConfig;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioType;
import cipm.consistency.tools.evaluation.scenario.data.teastore.LoadProfileType;
import cipm.consistency.tools.evaluation.scenario.helper.DefaultHttpClient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;

// covers both user load change and user behavior change
@Data
@Log
@EqualsAndHashCode(callSuper = true)
public class UserBehaviorChangeScenario extends AdaptionScenario {
	private static DefaultHttpClient http = new DefaultHttpClient(10000L);
	private static final int maxRetries = 5;
	private static final long WAIT_UNTIL_RETRY = 3000;

	private LoadProfileType loadType;

	public UserBehaviorChangeScenario(LoadProfileType type) {
		super(AdaptionScenarioType.WORKLOAD);
		this.loadType = type;
	}

	public UserBehaviorChangeScenario() {
		this(null);
	}

	@Override
	public void execute(AdaptionScenarioExecutionConfig config) {
		int tries = 0;
		boolean success = false;

		while (tries < maxRetries && !success) {

			try {
				String baseUrl = config.getLoadOrchestratorRestURL().endsWith("/") ? config.getLoadOrchestratorRestURL()
						: config.getLoadOrchestratorRestURL() + "/";
				if (loadType == LoadProfileType.NONE) {
					// stop it
					String finalUrl = baseUrl + "stop";
					if (http.isReachable(finalUrl)) {
						http.getRequest(finalUrl, Maps.newHashMap());
						http.getRequest(finalUrl, Maps.newHashMap());
					}
				} else {
					String finalUrl = baseUrl + "start";
					Map<String, String> attributes = Maps.newHashMap();
					attributes.put("file", loadType.getName());
					if (http.isReachable(finalUrl)) {
						http.getRequest(finalUrl, attributes);
						http.getRequest(finalUrl, attributes);
					}
				}
			} catch (Exception e) {
				log.warning("Failed to apply load (" + e.getClass().getName() + ").");
			}

			tries++;

			try {
				Thread.sleep(WAIT_UNTIL_RETRY);
			} catch (InterruptedException e) {
				log.warning("Failed to pause thread.");
			}
		}
	}

	@Override
	public InMemoryPCM generateReferenceModel(InMemoryPCM current) {
		// we do not investigate accuracy of usage models --> already done in iObserve
		// context
		// therefore return the input model
		return current;
	}

}
