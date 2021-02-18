package cipm.consistency.app.rest.health;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cipm.consistency.app.rest.health.data.JsonComponentHealthStateProblem;
import cipm.consistency.app.rest.health.data.JsonComponentHealthStateProblemContainer;
import cipm.consistency.app.rest.health.data.JsonComponentHealthStates;
import cipm.consistency.base.core.health.HealthStateManager;
import cipm.consistency.base.core.health.HealthStateObservedComponent;
import cipm.consistency.base.core.health.HealthStateProblem;
import cipm.consistency.base.shared.JsonUtil;

@RestController
public class ComponentHealthRestController {

	@Autowired
	private HealthStateManager healthStateManager;

	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping("/health/get")
	public String getHealthStates() {
		JsonComponentHealthStates states = new JsonComponentHealthStates();
		for (HealthStateObservedComponent comp : HealthStateObservedComponent.values()) {
			states.getStates().put(Pair.of(comp, comp.getName()), healthStateManager.getState(comp));
		}

		try {
			return objectMapper.writeValueAsString(states);
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("success", false, false);
		}
	}

	@GetMapping("/health/problems")
	public String getHealthProblems() {
		JsonComponentHealthStateProblemContainer container = new JsonComponentHealthStateProblemContainer();
		for (HealthStateProblem problem : healthStateManager.getProblems()) {
			JsonComponentHealthStateProblem jsonProblem = new JsonComponentHealthStateProblem();
			jsonProblem.setComponent(problem.getSource());
			jsonProblem.setSeverity(problem.getSeverity());
			jsonProblem.setMessage(problem.getDescription());
			jsonProblem.setId(problem.getId());
			container.getProblems().add(jsonProblem);
		}

		try {
			return objectMapper.writeValueAsString(container);
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("success", false, false);
		}
	}

}
