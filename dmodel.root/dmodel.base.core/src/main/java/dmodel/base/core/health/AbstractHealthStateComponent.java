package dmodel.base.core.health;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Abstract class that represents a component with a related health state.
 * Allows to define dependencies to other components and to automatically handle
 * and report errors.
 * 
 * @author David Monschein
 *
 */
public abstract class AbstractHealthStateComponent implements InitializingBean {
	/**
	 * The component type of this specific component.
	 */
	protected HealthStateObservedComponent component;

	/**
	 * All depdendencies of this specific component.
	 */
	private HealthStateObservedComponent[] dependencies;

	/**
	 * A set of IDs of the reported problems. Needed to remove them later on.
	 */
	private Set<Long> reportedProblems;

	/**
	 * Optional that contains a ID of the problem that has been reported due to
	 * dependency problems. If there is no problem with the dependencies, this
	 * optional is empty.
	 */
	private Optional<Long> dependencyProblem;

	/**
	 * Reference to the manager which saves and manages the health states of all
	 * components.
	 */
	@Autowired
	private HealthStateManager healthStateManager;

	/**
	 * Creates a new health state component.
	 * 
	 * @param component    the component type
	 * @param dependencies the component types that are required
	 */
	protected AbstractHealthStateComponent(HealthStateObservedComponent component,
			HealthStateObservedComponent... dependencies) {
		this.component = component;
		this.reportedProblems = Sets.newHashSet();
		this.dependencies = dependencies;
		this.dependencyProblem = Optional.empty();
	}

	/**
	 * Registers this component at the manager.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.healthStateManager.registerComponent(component, this);
	}

	/**
	 * Reacts on a message from another component.
	 * 
	 * @param source the source component that sends the message
	 * @param state  the new state of the component that sends the message
	 */
	protected abstract void onMessage(HealthStateObservedComponent source, HealthState state);

	/**
	 * Sends a message with the current state of this component to a given
	 * component.
	 * 
	 * @param to the component which should receive the current state of this
	 *           component
	 */
	protected void sendStateMessage(HealthStateObservedComponent to) {
		healthStateManager.sendMessage(component, to, healthStateManager.getState(component));
	}

	/**
	 * Checks all preconditions (dependencies) of this component and reports a
	 * problem if a dependency is missing.
	 * 
	 * @return true if all dependencies are satisfied, false otherwise
	 */
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

	/**
	 * Reports a dependency problem with a given component.
	 * 
	 * @param single the component
	 */
	protected void reportDependencyProblem(HealthStateObservedComponent single) {
		this.reportDependencyProblem(Lists.newArrayList(single));
	}

	/**
	 * Reports a single problem, containing dependency problems with several
	 * components.
	 * 
	 * @param comps the components
	 */
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

	/**
	 * Checks whether there are problems with any dependencies.
	 * 
	 * @param dependencies the dependencies
	 * @return true, if a dependency is not present or in an error state
	 */
	protected boolean checkDependenciesSimple(HealthStateObservedComponent... dependencies) {
		return this.checkDependencies(dependencies).size() > 0;
	}

	/**
	 * Similar to
	 * {@link AbstractHealthStateComponent#checkDependenciesSimple(HealthStateObservedComponent...)},
	 * but returns all dependencies that are not satisfied.
	 * 
	 * @param dependencies the dependencies
	 * @return a list of all dependencies that are not present or in an error state
	 */
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

	/**
	 * Updates the state of this component by evaluating all currently existing
	 * problems. For example, if there are none, the component is reported as
	 * working.
	 */
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

	/**
	 * Removes all problems of this component.
	 */
	protected void removeAllProblems() {
		reportedProblems.forEach(l -> healthStateManager.removeProblem(l));
		reportedProblems.clear();
	}

	/**
	 * Removes a problem with a given ID.
	 * 
	 * @param id the ID of the problem
	 */
	protected void removeProblem(long id) {
		healthStateManager.removeProblem(id);
		reportedProblems.remove(id);
	}

	/**
	 * Reports a problem to the health state manager.
	 * 
	 * @param problem the problem that should be reported
	 * @return a generated ID for the problem, which can be used to remove it later
	 */
	protected long reportProblem(HealthStateProblem problem) {
		long id = healthStateManager.addProblem(problem);
		reportedProblems.add(id);
		return id;
	}

	/**
	 * Reports a problem with a given message and severity with this component as
	 * source.
	 * 
	 * @param msg      the problem message
	 * @param severity the severity of the problem
	 * @return the generated ID of the problem, which can be used to remove it later
	 */
	protected long reportProblem(String msg, HealthStateProblemSeverity severity) {
		return this.reportProblem(
				HealthStateProblem.builder().description(msg).severity(severity).source(component).build());
	}

	/**
	 * Reports an error with a given message.
	 * 
	 * @param text the error message
	 * @return the ID of the problem, which can be used to remove it later
	 */
	protected long reportError(String text) {
		return this.reportProblem(text, HealthStateProblemSeverity.ERROR);
	}

	/**
	 * Builds a problem with this component as source.
	 * 
	 * @return the generated problem builder
	 */
	protected HealthStateProblem.HealthStateProblemBuilder buildHealthStateProblem() {
		return HealthStateProblem.builder().source(component);
	}

	/**
	 * Gets the state of this component.
	 * 
	 * @return the state of this component
	 */
	@JsonIgnore
	public HealthState getState() {
		return healthStateManager.getState(component);
	}

}
