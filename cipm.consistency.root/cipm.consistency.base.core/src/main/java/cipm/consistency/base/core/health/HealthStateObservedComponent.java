package cipm.consistency.base.core.health;

import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph;
import lombok.Getter;

/**
 * Enum for all component types that are involved in the approach.
 * 
 * @author David Monschein
 *
 */
public enum HealthStateObservedComponent {
	/**
	 * Component for managing the Virtual Single Underlying Model (VSUM) of
	 * Vitruvius.
	 */
	VSUM_MANAGER("VSUM Manager"),

	/**
	 * Contains the configuration and manages it.
	 */
	CONFIGURATION("Configuration"),

	/**
	 * Loads, stores and provides all model types.
	 */
	MODEL_MANAGER("Model Manager"),

	/**
	 * Parses the code of the Java project to be observed. And manages the
	 * corresponding files.
	 */
	PROJECT_MANAGER("Project Manager"),

	/**
	 * Builds an {@link ServiceCallGraph} from the source code.
	 */
	SCG_BUILDER("Service-Call-Graph Builder"),

	/**
	 * Responsible for the instrumentation of the project under observation.
	 */
	INSTRUMENTATION_MANAGER("Instrumentation Manager"),

	/**
	 * Builds a system model at design-time semi-automatically (user-guided).
	 */
	DT_SYSTEM_BUILDER("Design-Time System Model Extractor"),

	/**
	 * The Pipeline which processes the monitoring data.
	 */
	PIPELINE("Pipeline"),

	/**
	 * Executes validations and provides the results.
	 */
	VALIDATION_CONTROLLER("Validation Controller");

	/**
	 * Name of the component, which is displayed in the web UI.
	 */
	@Getter
	private String name;

	/**
	 * Creates new component type with a given name
	 * 
	 * @param name name of the component type
	 */
	private HealthStateObservedComponent(String name) {
		this.name = name;
	}
}
