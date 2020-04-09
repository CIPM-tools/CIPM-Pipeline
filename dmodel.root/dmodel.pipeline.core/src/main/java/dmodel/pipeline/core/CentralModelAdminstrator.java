package dmodel.pipeline.core;

import java.io.File;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.springframework.stereotype.Component;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import dmodel.pipeline.shared.FileBackedModelUtil;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.config.ModelConfiguration;
import dmodel.pipeline.shared.health.AbstractHealthStateComponent;
import dmodel.pipeline.shared.health.HealthStateObservedComponents;
import dmodel.pipeline.shared.health.HealthStateProblemSeverity;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pcm.LocalFilesystemPCM;
import tools.vitruv.framework.correspondence.CorrespondenceFactory;
import tools.vitruv.framework.correspondence.Correspondences;

@Component
public class CentralModelAdminstrator extends AbstractHealthStateComponent
		implements IPcmModelProvider, ISpecificModelProvider {
	private InMemoryPCM architectureModel;
	private LocalFilesystemPCM filesystemPCM;

	private InstrumentationModel instrumentationModel;
	private RuntimeEnvironmentModel runtimeEnvironmentModel;
	private Correspondences correspondenceModel;

	private File instrumentationModelFile;
	private File runtimeEnvironmentModelFile;
	private File correspondenceModelFile;

	public CentralModelAdminstrator() {
		super(HealthStateObservedComponents.CONFIGURATION);
	}

	public void loadArchitectureModel(ModelConfiguration config) {
		buildFileSystemPCM(config);
		buildRemainingModels(config);

		if (!configurationValid()) {
			reportConfigurationIncomplete();
			return;
		}

		// clear the old listeners (memory leak)
		clearSynchronization();
		architectureModel = InMemoryPCM.createFromFilesystemSynced(filesystemPCM);

		// sync models
		syncRemainingModels();

		// configuration for models okay
		reportConfigurationWorking();
	}

	public void swapCorrespondenceModel(Correspondences _new) {
		FileBackedModelUtil.clear(instrumentationModel);
		this.correspondenceModel = _new;
		FileBackedModelUtil.synchronize(correspondenceModel, correspondenceModelFile, Correspondences.class);
	}

	private void clearSynchronization() {
		if (architectureModel != null) {
			architectureModel.clearListeners();
		}
		FileBackedModelUtil.clear(correspondenceModel);
		FileBackedModelUtil.clear(instrumentationModel);
		FileBackedModelUtil.clear(runtimeEnvironmentModel);
	}

	private boolean configurationValid() {
		// correspondences may not exist
		return filesystemPCM.isValid() && instrumentationModel != null && runtimeEnvironmentModel != null;
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
	}

	private void reportConfigurationIncomplete() {
		super.reportProblem(
				super.buildHealthStateProblem().description("Model configuration is invalid or not complete.")
						.severity(HealthStateProblemSeverity.ERROR).build());
		super.updateState();
	}

	@Override
	public InstrumentationModel getInstrumentation() {
		return instrumentationModel;
	}

	@Override
	public RuntimeEnvironmentModel getRuntimeEnvironment() {
		return runtimeEnvironmentModel;
	}

	@Override
	public Correspondences getCorrespondences() {
		return correspondenceModel;
	}

	@Override
	public Repository getRepository() {
		return architectureModel.getRepository();
	}

	@Override
	public System getSystem() {
		return architectureModel.getSystem();
	}

	@Override
	public ResourceEnvironment getResourceEnvironment() {
		return architectureModel.getResourceEnvironmentModel();
	}

	@Override
	public Allocation getAllocation() {
		return architectureModel.getAllocationModel();
	}

	@Override
	public UsageModel getUsage() {
		return architectureModel.getUsageModel();
	}

	@Override
	public InMemoryPCM getRaw() {
		return architectureModel;
	}

}
