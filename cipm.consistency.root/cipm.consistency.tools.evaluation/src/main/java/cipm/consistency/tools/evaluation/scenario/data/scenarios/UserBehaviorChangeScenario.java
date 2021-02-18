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

// covers both user load change and user behavior change
@Data
@EqualsAndHashCode(callSuper = true)
public class UserBehaviorChangeScenario extends AdaptionScenario {
	private static DefaultHttpClient http = new DefaultHttpClient(5000L);

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
		String baseUrl = config.getLoadOrchestratorRestURL().endsWith("/") ? config.getLoadOrchestratorRestURL()
				: config.getLoadOrchestratorRestURL() + "/";
		if (loadType == LoadProfileType.NONE) {
			// stop it
			String finalUrl = baseUrl + "stop";
			http.getRequest(finalUrl, Maps.newHashMap());
		} else {
			String finalUrl = baseUrl + "start";
			Map<String, String> attributes = Maps.newHashMap();
			attributes.put("file", loadType.getName());
			http.getRequest(finalUrl, attributes);
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
