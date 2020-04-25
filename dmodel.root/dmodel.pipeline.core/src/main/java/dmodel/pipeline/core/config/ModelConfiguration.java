package dmodel.pipeline.core.config;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import dmodel.pipeline.shared.ModelUtil;
import lombok.Data;
import tools.vitruv.framework.correspondence.Correspondences;

/**
 * Configuration of the model paths.
 * 
 * @author David Monschein
 *
 */
@Data
public class ModelConfiguration {
	/**
	 * Path of the file where the {@link Repository} model should be stored.
	 */
	private String repositoryPath = null;

	/**
	 * Path of the file where the {@link System} model should be stored.
	 */
	private String systemPath = null;

	/**
	 * Path of the file where the {@link UsageModel} should be stored.
	 */
	private String usagePath = null;

	/**
	 * Path of the file where the {@link Allocation} model should be stored.
	 */
	private String allocationPath = null;

	/**
	 * Path of the file where the {@link ResourceEnvironment} model should be
	 * stored.
	 */
	private String envPath = null;

	/**
	 * /** Path of the file where the {@link InstrumentationModel} should be stored.
	 */
	private String instrumentationModelPath = null;

	/**
	 * Path of the file where the {@link Correspondences} should be stored.
	 */
	private String correspondencePath = null;

	/**
	 * Path of the file where the {@link RuntimeEnvironmentModel} should be stored.
	 */
	private String runtimeEnvironmentPath = null;

	/**
	 * Determines whether the current model configuration is valid.
	 * 
	 * @return true if all paths are valid, false otherwise
	 */
	@JsonIgnore
	public boolean isValid() {
		boolean repositoryValid = ModelUtil.validateModelPath(repositoryPath, Repository.class);
		boolean systemValid = ModelUtil.validateModelPath(systemPath, System.class);
		boolean usageValid = ModelUtil.validateModelPath(usagePath, UsageModel.class);
		boolean allocationValid = ModelUtil.validateModelPath(allocationPath, Allocation.class);
		boolean environmentValid = ModelUtil.validateModelPath(envPath, ResourceEnvironment.class);

		boolean instrumentationModelValid = ModelUtil.validateModelPath(instrumentationModelPath,
				InstrumentationModel.class);
		boolean correspondenceValid = ModelUtil.validateModelPath(correspondencePath, Correspondences.class);
		boolean runtimeEnvironmentValid = ModelUtil.validateModelPath(runtimeEnvironmentPath,
				RuntimeEnvironmentModel.class);

		return repositoryValid && systemValid && usageValid && allocationValid && environmentValid
				&& instrumentationModelValid && correspondenceValid && runtimeEnvironmentValid;
	}

}
