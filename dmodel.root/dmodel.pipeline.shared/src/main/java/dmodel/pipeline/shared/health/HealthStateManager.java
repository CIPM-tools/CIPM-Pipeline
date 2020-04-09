package dmodel.pipeline.shared.health;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Getter;

@Component
public class HealthStateManager implements InitializingBean {

	private Map<HealthStateObservedComponents, HealthState> stateMapping;

	@Getter
	private List<HealthStateProblem> problems;

	private long problemId = 0;

	@Override
	public void afterPropertiesSet() throws Exception {
		problems = Lists.newArrayList();
		stateMapping = Maps.newHashMap();
		for (HealthStateObservedComponents observedComponent : HealthStateObservedComponents.values()) {
			stateMapping.put(observedComponent, HealthState.UNKNOWN);
		}
	}

	public List<HealthStateProblem> getProblems(HealthStateObservedComponents comp) {
		return problems.stream().filter(p -> p.getSource() == comp).collect(Collectors.toList());
	}

	public HealthState getState(HealthStateObservedComponents comp) {
		return stateMapping.get(comp);
	}

	public boolean anyProblems() {
		return problems.size() > 0;
	}

	public void update(HealthStateObservedComponents vsumManager, HealthState value) {
		stateMapping.put(vsumManager, value);
	}

	public long addProblem(HealthStateProblem problem) {
		long selectedId = problemId++;
		problem.setId(selectedId);
		problems.add(problem);
		return selectedId;
	}

	public void removeAllProblems(HealthStateObservedComponents comp) {
		problems = problems.stream().filter(prob -> prob.getSource() != comp).collect(Collectors.toList());
	}

	public void removeProblem(long id) {
		problems = problems.stream().filter(prob -> prob.getId() != id).collect(Collectors.toList());
	}

}
