package dmodel.pipeline.core.health;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Getter;

@Component
public class HealthStateManager {

	private Map<HealthStateObservedComponent, HealthState> stateMapping;

	@Getter
	private List<HealthStateProblem> problems;

	private Map<HealthStateObservedComponent, AbstractHealthStateComponent> registeredComponents;

	private long problemId = 0;

	public HealthStateManager() {
		registeredComponents = Maps.newHashMap();
		problems = Lists.newArrayList();
		stateMapping = Maps.newHashMap();
		for (HealthStateObservedComponent observedComponent : HealthStateObservedComponent.values()) {
			stateMapping.put(observedComponent, HealthState.UNKNOWN);
		}
	}

	public AbstractHealthStateComponent getRegisteredComponent(HealthStateObservedComponent comp) {
		return registeredComponents.get(comp);
	}

	public void registerComponent(HealthStateObservedComponent comp, AbstractHealthStateComponent value) {
		registeredComponents.put(comp, value);
	}

	public List<HealthStateProblem> getProblems(HealthStateObservedComponent comp) {
		return problems.stream().filter(p -> p.getSource() == comp).collect(Collectors.toList());
	}

	public HealthState getState(HealthStateObservedComponent comp) {
		return stateMapping.get(comp);
	}

	public boolean anyProblems() {
		return problems.size() > 0;
	}

	public void update(HealthStateObservedComponent vsumManager, HealthState value) {
		stateMapping.put(vsumManager, value);
	}

	public long addProblem(HealthStateProblem problem) {
		long selectedId = problemId++;
		problem.setId(selectedId);
		problems.add(problem);
		return selectedId;
	}

	public void removeAllProblems(HealthStateObservedComponent comp) {
		problems = problems.stream().filter(prob -> prob.getSource() != comp).collect(Collectors.toList());
	}

	public void removeProblem(long id) {
		problems = problems.stream().filter(prob -> prob.getId() != id).collect(Collectors.toList());
	}

}
