package dmodel.pipeline.core.config;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import dmodel.pipeline.shared.ModelUtil;
import lombok.Data;
import tools.vitruv.framework.correspondence.Correspondences;

@Data
public class ModelConfiguration {

	private String repositoryPath = null;
	private String systemPath = null;
	private String usagePath = null;
	private String allocationPath = null;
	private String envPath = null;

	private String instrumentationModelPath = null;
	private String correspondencePath = null;
	private String runtimeEnvironmentPath = null;

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
