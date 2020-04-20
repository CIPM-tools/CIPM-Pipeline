package dmodel.pipeline.core.health;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
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
	private Map<HealthStateObservedComponent, List<Pair<HealthStateObservedComponent, HealthState>>> queuedMesages;

	private long problemId = 0;

	public HealthStateManager() {
		registeredComponents = Maps.newHashMap();
		problems = Lists.newArrayList();
		stateMapping = Maps.newHashMap();
		queuedMesages = Maps.newHashMap();
		for (HealthStateObservedComponent observedComponent : HealthStateObservedComponent.values()) {
			stateMapping.put(observedComponent, HealthState.UNKNOWN);
		}
	}

	public void sendMessage(HealthStateObservedComponent from, HealthStateObservedComponent to, HealthState state) {
		if (registeredComponents.containsKey(to)) {
			registeredComponents.get(to).onMessage(from, state);
		} else {
			if (queuedMesages.containsKey(to)) {
				queuedMesages.get(to).add(Pair.of(from, state));
			} else {
				queuedMesages.put(to, Lists.newArrayList(Pair.of(from, state)));
			}
		}
	}

	public AbstractHealthStateComponent getRegisteredComponent(HealthStateObservedComponent comp) {
		return registeredComponents.get(comp);
	}

	public void registerComponent(HealthStateObservedComponent comp, AbstractHealthStateComponent value) {
		registeredComponents.put(comp, value);

		if (queuedMesages.containsKey(comp)) {
			queuedMesages.get(comp).forEach(msg -> value.onMessage(msg.getLeft(), msg.getRight()));
			queuedMesages.remove(comp);
		}
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
