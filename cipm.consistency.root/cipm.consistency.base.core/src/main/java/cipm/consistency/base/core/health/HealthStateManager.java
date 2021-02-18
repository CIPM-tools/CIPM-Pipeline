package cipm.consistency.base.core.health;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Getter;

/**
 * Manages the health states of all components and routes message to them.
 * 
 * @author David Monschein
 *
 */
@Component
public class HealthStateManager {
	/**
	 * Mapping between component -> health state.
	 */
	private Map<HealthStateObservedComponent, HealthState> stateMapping;

	/**
	 * List of all currently existing problems.
	 */
	@Getter
	private List<HealthStateProblem> problems;

	/**
	 * Mapping between component type -> registered component.
	 */
	private Map<HealthStateObservedComponent, AbstractHealthStateComponent> registeredComponents;

	/**
	 * Queue for messages that should be routed to a certain component. This is
	 * necessary because the component is not registered yet and there are already
	 * messages for it.
	 */
	private Map<HealthStateObservedComponent, List<Pair<HealthStateObservedComponent, HealthState>>> queuedMesages;

	/**
	 * The ID for the next generated problem.
	 */
	private long problemId = 0;

	/**
	 * Creates a new empty instance with no registered components and states.
	 */
	public HealthStateManager() {
		registeredComponents = Maps.newHashMap();
		problems = Lists.newArrayList();
		stateMapping = Maps.newHashMap();
		queuedMesages = Maps.newHashMap();
		for (HealthStateObservedComponent observedComponent : HealthStateObservedComponent.values()) {
			stateMapping.put(observedComponent, HealthState.UNKNOWN);
		}
	}

	/**
	 * Sends a message from a given component to a specific component with an
	 * associated state.
	 * 
	 * @param from  the source component of the message
	 * @param to    the target component of the message
	 * @param state the state that should be send to the target component
	 */
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

	/**
	 * Gets the registered component for a given type.
	 * 
	 * @param comp the component type
	 * @return the registered component, or null if there is none registered yet
	 */
	public AbstractHealthStateComponent getRegisteredComponent(HealthStateObservedComponent comp) {
		return registeredComponents.get(comp);
	}

	/**
	 * Registers a component for a given type.
	 * 
	 * @param comp  the type of the component
	 * @param value the component to register
	 */
	public void registerComponent(HealthStateObservedComponent comp, AbstractHealthStateComponent value) {
		registeredComponents.put(comp, value);

		if (queuedMesages.containsKey(comp)) {
			queuedMesages.get(comp).forEach(msg -> value.onMessage(msg.getLeft(), msg.getRight()));
			queuedMesages.remove(comp);
		}
	}

	/**
	 * Gets all problems of a specific component type.
	 * 
	 * @param comp the component
	 * @return all problems of the component
	 */
	public List<HealthStateProblem> getProblems(HealthStateObservedComponent comp) {
		return problems.stream().filter(p -> p.getSource() == comp).collect(Collectors.toList());
	}

	/**
	 * Gets the state of a given component type.
	 * 
	 * @param comp the component type
	 * @return the state of the registered component for the given type
	 */
	public HealthState getState(HealthStateObservedComponent comp) {
		return stateMapping.get(comp);
	}

	/**
	 * Determines whether there are any problems.
	 * 
	 * @return true, if there is a problem or multiple ones, false if not
	 */
	public boolean anyProblems() {
		return problems.size() > 0;
	}

	/**
	 * Updates the state of a given component.
	 * 
	 * @param vsumManager the component
	 * @param value       the new state
	 */
	public void update(HealthStateObservedComponent vsumManager, HealthState value) {
		stateMapping.put(vsumManager, value);
	}

	/**
	 * Adds a problem.
	 * 
	 * @param problem the problem to add
	 * @return a generated ID for the problem which can be used to remove it later
	 */
	public long addProblem(HealthStateProblem problem) {
		long selectedId = problemId++;
		problem.setId(selectedId);
		problems.add(problem);
		return selectedId;
	}

	/**
	 * Removes all problems of a specific component type
	 * 
	 * @param comp the component type
	 */
	public void removeAllProblems(HealthStateObservedComponent comp) {
		problems = problems.stream().filter(prob -> prob.getSource() != comp).collect(Collectors.toList());
	}

	/**
	 * Removes a problem with a given ID.
	 * 
	 * @param id the ID of the problem to remove
	 */
	public void removeProblem(long id) {
		problems = problems.stream().filter(prob -> prob.getId() != id).collect(Collectors.toList());
	}

}
