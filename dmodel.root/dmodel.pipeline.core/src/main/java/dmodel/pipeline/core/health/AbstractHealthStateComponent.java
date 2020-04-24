package dmodel.pipeline.core.health;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class AbstractHealthStateComponent implements InitializingBean {
	protected HealthStateObservedComponent component;
	private HealthStateObservedComponent[] dependencies;

	private Set<Long> reportedProblems;
	private Optional<Long> dependencyProblem;

	@Autowired
	private HealthStateManager healthStateManager;

	protected AbstractHealthStateComponent(HealthStateObservedComponent component,
			HealthStateObservedComponent... dependencies) {
		this.component = component;
		this.reportedProblems = Sets.newHashSet();
		this.dependencies = dependencies;
		this.dependencyProblem = Optional.empty();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.healthStateManager.registerComponent(component, this);
	}

	protected abstract void onMessage(HealthStateObservedComponent source, HealthState state);

	protected void sendStateMessage(HealthStateObservedComponent to) {
		healthStateManager.sendMessage(component, to, healthStateManager.getState(component));
	}

	protected boolean checkPreconditions() {
		List<HealthStateObservedComponent> probs = checkDependencies(dependencies);
		if (probs.size() > 0) {
			// we have an old one
			if (this.dependencyProblem.isPresent()) {
				return false;
			}

			this.reportDependencyProblem(probs);
			return false;
		}

		// remove old one
		if (this.dependencyProblem.isPresent()) {
			this.removeProblem(this.dependencyProblem.get());
		}

		return true;
	}

	protected void reportDependencyProblem(HealthStateObservedComponent single) {
		this.reportDependencyProblem(Lists.newArrayList(single));
	}

	protected void reportDependencyProblem(List<HealthStateObservedComponent> comps) {
		String compProbString = "";
		for (HealthStateObservedComponent comp : comps) {
			compProbString += ", " + comp.getName();
		}
		if (comps.size() > 0) {
			compProbString = compProbString.substring(2);
		}

		HealthStateProblem nProblem = this.buildHealthStateProblem()
				.description("There are problems with needed components (" + compProbString
						+ "). Either a dependency is in an error state or the state could not be retrieved.")
				.severity(HealthStateProblemSeverity.ERROR).build();

		this.dependencyProblem = Optional.of(this.reportProblem(nProblem));
		this.updateState();
	}

	protected boolean checkDependenciesSimple(HealthStateObservedComponent... dependencies) {
		return this.checkDependencies(dependencies).size() > 0;
	}

	protected List<HealthStateObservedComponent> checkDependencies(HealthStateObservedComponent... dependencies) {
		List<HealthStateObservedComponent> problems = new ArrayList<>();
		for (HealthStateObservedComponent dep : dependencies) {
			if (healthStateManager.getState(dep) == HealthState.ERROR
					|| healthStateManager.getState(dep) == HealthState.UNKNOWN) {
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

	protected long reportProblem(String msg, HealthStateProblemSeverity severity) {
		return this.reportProblem(
				HealthStateProblem.builder().description(msg).severity(severity).source(component).build());
	}

	protected long reportError(String text) {
		return this.reportProblem(text, HealthStateProblemSeverity.ERROR);
	}

	protected HealthStateProblem.HealthStateProblemBuilder buildHealthStateProblem() {
		return HealthStateProblem.builder().source(component);
	}

	public HealthState getState() {
		return healthStateManager.getState(component);
	}

}
