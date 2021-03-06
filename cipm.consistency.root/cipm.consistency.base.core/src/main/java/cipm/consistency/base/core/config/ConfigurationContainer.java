package cipm.consistency.base.core.config;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import cipm.consistency.base.core.health.AbstractHealthStateComponent;
import cipm.consistency.base.core.health.HealthState;
import cipm.consistency.base.core.health.HealthStateManager;
import cipm.consistency.base.core.health.HealthStateObservedComponent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The main configuration container for the application. It mainly consists of
 * four parts:
 * <ul>
 * <li>Project configuration</li>
 * <li>Model configurations (paths)</li>
 * <li>Monitoring data collection configuration</li>
 * <li>Configuration of the validation feedback loop (VFL)</li>
 * </ul>
 * 
 * @author David Monschein
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class ConfigurationContainer extends AbstractHealthStateComponent {
	// project config
	/**
	 * Project configuration (path and source folders).
	 */
	private ProjectConfiguration project;

	// model config
	/**
	 * Configuration of the models (file paths).
	 */
	private ModelConfiguration models;

	// processing md config
	/**
	 * Configuration of the monitoring data collection.
	 */
	private MonitoringDataEntryConfiguration entry;

	// vfl
	/**
	 * Configuration of the validation feedback loop (VFL).
	 */
	private ValidationFeedbackLoopConfiguration vfl;

	/**
	 * Configuration of the rules for the instrumentation model transformation.
	 */
	private PredicateRuleConfiguration validationPredicates;

	/**
	 * Configuration for the calibration of the models.
	 */
	private ModelCalibrationConfiguration calibration = new ModelCalibrationConfiguration();

	// create writer
	/**
	 * Object mapper for serializing and deserializing JSON objects.
	 */
	@JsonIgnore
	private ObjectMapper writer = new ObjectMapper(new YAMLFactory());

	/**
	 * Creates a new configuration container.
	 */
	public ConfigurationContainer() {
		super(HealthStateObservedComponent.CONFIGURATION);
	}

	/**
	 * Validates the configuration after the application started.
	 */
	@PostConstruct
	public void initialValidation() {
		this.validateConfiguration();
	}

	/**
	 * Validates the configuration and reports problems to the
	 * {@link HealthStateManager}.
	 */
	public void validateConfiguration() {
		this.removeAllProblems();

		boolean projectValid = project.isValid();
		boolean modelsValid = models.isValid();
		boolean entryValid = entry.isValid();
		boolean vflValid = vfl.isValid();

		if (!projectValid) {
			super.reportError("The project configuration is not valid.");
		}
		if (!modelsValid) {
			super.reportError("The model configuration is not valid.");
		}
		if (!entryValid) {
			super.reportError("The configuration of the monitoring data entry point is not valid.");
		}
		if (!vflValid) {
			super.reportError("The configuration of the validation feedback loop (VFL) is not valid.");
		}

		super.updateState();

		if (projectValid && modelsValid && entryValid && vflValid) {
			this.sendStateMessage(HealthStateObservedComponent.MODEL_MANAGER);
			this.sendStateMessage(HealthStateObservedComponent.PROJECT_MANAGER);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		// NOTHING TO DO HERE
	}

}
