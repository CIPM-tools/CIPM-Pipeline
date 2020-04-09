package dmodel.pipeline.shared.health;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class AbstractHealthStateComponent {
	private HealthStateObservedComponents component;
	private HealthStateObservedComponents[] dependencies;

	private Set<Long> reportedProblems;

	@Autowired
	private HealthStateManager healthStateManager;

	protected AbstractHealthStateComponent(HealthStateObservedComponents component,
			HealthStateObservedComponents... dependencies) {
		this.component = component;
		this.reportedProblems = Sets.newHashSet();
		this.dependencies = dependencies;
	}

	protected boolean checkPreconditions() {
		List<HealthStateObservedComponents> probs = checkDependencies(dependencies);
		if (probs.size() > 0) {
			this.reportDependencyProblem(probs);
			return false;
		}
		return true;
	}

	protected void reportDependencyProblem(HealthStateObservedComponents single) {
		this.reportDependencyProblem(Lists.newArrayList(single));
	}

	protected void reportDependencyProblem(List<HealthStateObservedComponents> comps) {
		String compProbString = "";
		for (HealthStateObservedComponents comp : comps) {
			compProbString += ", " + comp.getName();
		}
		if (comps.size() > 0) {
			compProbString = compProbString.substring(2);
		}

		HealthStateProblem nProblem = this.buildHealthStateProblem()
				.description("There are problems with needed components (" + compProbString + ").")
				.severity(HealthStateProblemSeverity.ERROR).build();
		this.reportProblem(nProblem);
	}

	protected boolean checkDependenciesSimple(HealthStateObservedComponents... dependencies) {
		return this.checkDependencies(dependencies).size() > 0;
	}

	protected List<HealthStateObservedComponents> checkDependencies(HealthStateObservedComponents... dependencies) {
		List<HealthStateObservedComponents> problems = new ArrayList<>();
		for (HealthStateObservedComponents dep : dependencies) {
			if (healthStateManager.getState(dep) == HealthState.ERROR) {
				problems.add(dep);
			}
		}
		return problems;
	}

	protected void updateState() {
		List<HealthStateProblem> problems = healthStateManager.getProblems(component);
		if (problems.size() == 0) {
			healthStateManager.update(component, HealthState.WORKING);
		} else {
			boolean anyError = problems.stream().anyMatch(p -> p.getSeverity() == HealthStateProblemSeverity.ERROR);
			boolean anyWarning = problems.stream().anyMatch(p -> p.getSeverity() == HealthStateProblemSeverity.WARNING);

			if (anyError) {
				healthStateManager.update(component, HealthState.ERROR);
			} else if (anyWarning) {
				healthStateManager.update(component, HealthState.WARNING);
			} else {
				healthStateManager.update(component, HealthState.WORKING);
			}
		}
	}

	protected void removeAllProblems() {
		reportedProblems.forEach(l -> healthStateManager.removeProblem(l));
		reportedProblems.clear();
	}

	protected void removeProblem(long id) {
		healthStateManager.removeProblem(id);
		reportedProblems.remove(id);
	}

	protected long reportProblem(HealthStateProblem problem) {
		long id = healthStateManager.addProblem(problem);
		reportedProblems.add(id);
		return id;
	}

	protected HealthStateProblem.HealthStateProblemBuilder buildHealthStateProblem() {
		return HealthStateProblem.builder().source(component);
	}

}
