package dmodel.pipeline.core.config;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import dmodel.pipeline.core.health.AbstractHealthStateComponent;
import dmodel.pipeline.core.health.HealthState;
import dmodel.pipeline.core.health.HealthStateObservedComponent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class ConfigurationContainer extends AbstractHealthStateComponent {
	// project config
	private ProjectConfiguration project;

	// model config
	private ModelConfiguration models;

	// processing md config
	private MonitoringDataEntryConfiguration entry;

	// vfl
	private ValidationFeedbackLoopConfiguration vfl;

	// path of the file
	@JsonIgnore
	private File fileBackedPath;

	// create writer
	@JsonIgnore
	private ObjectMapper writer = new ObjectMapper(new YAMLFactory());

	public ConfigurationContainer() {
		super(HealthStateObservedComponent.CONFIGURATION);
	}

	@PostConstruct
	public void initialValidation() {
		this.validateConfiguration();
	}

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

	public boolean syncWithFilesystem() {
		this.validateConfiguration();

		writer.findAndRegisterModules();
		try {
			writer.writeValue(fileBackedPath, this);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		// NOTHING TO DO HERE
	}

}
