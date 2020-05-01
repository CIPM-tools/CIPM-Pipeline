package dmodel.base.core.impl;

import java.io.File;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.base.core.IPcmModelProvider;
import dmodel.base.core.ISpecificModelProvider;
import dmodel.base.core.config.ConfigurationContainer;
import dmodel.base.core.config.ModelConfiguration;
import dmodel.base.core.health.AbstractHealthStateComponent;
import dmodel.base.core.health.HealthState;
import dmodel.base.core.health.HealthStateObservedComponent;
import dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import dmodel.base.shared.FileBackedModelUtil;
import dmodel.base.shared.ModelUtil;
import dmodel.base.shared.pcm.InMemoryPCM;
import dmodel.base.shared.pcm.LocalFilesystemPCM;
import tools.vitruv.framework.correspondence.CorrespondenceFactory;
import tools.vitruv.framework.correspondence.Correspondences;

@Component
public class CentralModelAdminstrator extends AbstractHealthStateComponent
		implements IPcmModelProvider, ISpecificModelProvider {
	@Autowired
	private ConfigurationContainer configuration;

	private InMemoryPCM architectureModel;
	private LocalFilesystemPCM filesystemPCM;

	private InstrumentationModel instrumentationModel;
	private RuntimeEnvironmentModel runtimeEnvironmentModel;
	private Correspondences correspondenceModel;

	private File instrumentationModelFile;
	private File runtimeEnvironmentModelFile;
	private File correspondenceModelFile;

	public CentralModelAdminstrator() {
		super(HealthStateObservedComponent.MODEL_MANAGER, HealthStateObservedComponent.CONFIGURATION);
	}

	public void loadArchitectureModel(ModelConfiguration config) {
		if (!checkPreconditions()) {
			return;
		}

		buildFileSystemPCM(config);
		buildRemainingModels(config);

		// clear the old listeners (memory leak)
		clearSynchronization();
		architectureModel = InMemoryPCM.createFromFilesystemSynced(filesystemPCM);

		// set corresponding models for allocation
		architectureModel.getAllocationModel()
				.setTargetResourceEnvironment_Allocation(architectureModel.getResourceEnvironmentModel());
		architectureModel.getAllocationModel().setSystem_Allocation(architectureModel.getSystem());

		// sync models
		syncRemainingModels();

		// configuration for models okay
		reportConfigurationWorking();
	}

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		if (source == HealthStateObservedComponent.CONFIGURATION && state == HealthState.WORKING) {
			loadArchitectureModel(configuration.getModels());
		}
	}

	private void clearSynchronization() {
		if (architectureModel != null) {
			architectureModel.clearListeners();
		}
		FileBackedModelUtil.clear(correspondenceModel);
		FileBackedModelUtil.clear(instrumentationModel);
		FileBackedModelUtil.clear(runtimeEnvironmentModel);
	}

	private void syncRemainingModels() {
		if (correspondenceModel == null) {
			correspondenceModel = CorrespondenceFactory.eINSTANCE.createCorrespondences();
		}

		FileBackedModelUtil.synchronize(instrumentationModel, instrumentationModelFile, InstrumentationModel.class);
		FileBackedModelUtil.synchronize(correspondenceModel, correspondenceModelFile, Correspondences.class);
		FileBackedModelUtil.synchronize(runtimeEnvironmentModel, runtimeEnvironmentModelFile,
				RuntimeEnvironmentModel.class);
	}

	private void buildRemainingModels(ModelConfiguration config) {
		instrumentationModelFile = new File(config.getInstrumentationModelPath());
		runtimeEnvironmentModelFile = new File(config.getRuntimeEnvironmentPath());
		correspondenceModelFile = new File(config.getCorrespondencePath());

		instrumentationModel = ModelUtil.readFromFile(instrumentationModelFile, InstrumentationModel.class);
		runtimeEnvironmentModel = ModelUtil.readFromFile(runtimeEnvironmentModelFile, RuntimeEnvironmentModel.class);
		correspondenceModel = ModelUtil.readFromFile(correspondenceModelFile, Correspondences.class);
	}

	private void buildFileSystemPCM(ModelConfiguration config) {
		filesystemPCM = new LocalFilesystemPCM();
		filesystemPCM.setAllocationModelFile(
				config.getAllocationPath().length() > 0 ? new File(config.getAllocationPath()) : null);
		filesystemPCM.setRepositoryFile(
				config.getRepositoryPath().length() > 0 ? new File(config.getRepositoryPath()) : null);
		filesystemPCM
				.setResourceEnvironmentFile(config.getEnvPath().length() > 0 ? new File(config.getEnvPath()) : null);
		filesystemPCM.setSystemFile(config.getSystemPath().length() > 0 ? new File(config.getSystemPath()) : null);
		filesystemPCM.setUsageModelFile(config.getUsagePath().length() > 0 ? new File(config.getUsagePath()) : null);
	}

	private void reportConfigurationWorking() {
		super.removeAllProblems();
		super.updateState();
		super.sendStateMessage(HealthStateObservedComponent.VSUM_MANAGER);
	}

	@Override
	public InstrumentationModel getInstrumentation() {
		return architectureModel != null ? instrumentationModel : null;
	}

	@Override
	public RuntimeEnvironmentModel getRuntimeEnvironment() {
		return architectureModel != null ? runtimeEnvironmentModel : null;
	}

	@Override
	public Correspondences getCorrespondences() {
		return architectureModel != null ? correspondenceModel : null;
	}

	@Override
	public Repository getRepository() {
		return architectureModel != null ? architectureModel.getRepository() : null;
	}

	@Override
	public System getSystem() {
		return architectureModel != null ? architectureModel.getSystem() : null;
	}

	@Override
	public ResourceEnvironment getResourceEnvironment() {
		return architectureModel != null ? architectureModel.getResourceEnvironmentModel() : null;
	}

	@Override
	public Allocation getAllocation() {
		return architectureModel != null ? architectureModel.getAllocationModel() : null;
	}

	@Override
	public UsageModel getUsage() {
		return architectureModel != null ? architectureModel.getUsageModel() : null;
	}

	@Override
	public InMemoryPCM getRaw() {
		return architectureModel;
	}

}
