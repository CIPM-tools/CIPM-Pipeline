package cipm.consistency.base.core.impl;

import java.io.File;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.core.ISpecificModelProvider;
import cipm.consistency.base.core.config.ConfigurationContainer;
import cipm.consistency.base.core.config.ModelConfiguration;
import cipm.consistency.base.core.health.AbstractHealthStateComponent;
import cipm.consistency.base.core.health.HealthState;
import cipm.consistency.base.core.health.HealthStateObservedComponent;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import cipm.consistency.base.shared.FileBackedModelUtil;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.base.shared.pcm.LocalFilesystemPCM;
import tools.vitruv.framework.correspondence.CorrespondenceFactory;
import tools.vitruv.framework.correspondence.Correspondences;

/**
 * Component that manages all models. It synchronizes them with files and
 * provides them for other components.
 * 
 * @author David Monschein
 *
 */
@Component
public class CentralModelProviderImpl extends AbstractHealthStateComponent
		implements IPcmModelProvider, ISpecificModelProvider {
	/**
	 * Reference to the configuration.
	 */
	@Autowired
	private ConfigurationContainer configuration;

	/**
	 * Underlying PCM model.
	 */
	private InMemoryPCM architectureModel;

	/**
	 * File system paths for the PCM models (for synchronization).
	 */
	private LocalFilesystemPCM filesystemPCM;

	/**
	 * Instrumentation model.
	 */
	private InstrumentationModel instrumentationModel;

	/**
	 * Runtime Environment model.
	 */
	private RuntimeEnvironmentModel runtimeEnvironmentModel;

	/**
	 * Correspondence model which is used by the VSUM manager.
	 */
	private Correspondences correspondenceModel;

	/**
	 * File that is used to synchronize the instrumentation model.
	 */
	private File instrumentationModelFile;

	/**
	 * File that is used to synchronize the runtime environment model.
	 */
	private File runtimeEnvironmentModelFile;

	/**
	 * File that is used to store the correspondences.
	 */
	private File correspondenceModelFile;

	/**
	 * Creates a new instance and registers it as model manager component.
	 */
	public CentralModelProviderImpl() {
		super(HealthStateObservedComponent.MODEL_MANAGER, HealthStateObservedComponent.CONFIGURATION);
	}

	/**
	 * Loads all models from a given configuration which contains the paths.
	 * 
	 * @param config the configuration which contains the paths
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		if (source == HealthStateObservedComponent.CONFIGURATION && state == HealthState.WORKING) {
			loadArchitectureModel(configuration.getModels());
		}
	}

	/**
	 * Stops the synchronization of the models with the files.
	 */
	private void clearSynchronization() {
		if (architectureModel != null) {
			architectureModel.clearListeners();
		}
		FileBackedModelUtil.clear(correspondenceModel);
		FileBackedModelUtil.clear(instrumentationModel);
		FileBackedModelUtil.clear(runtimeEnvironmentModel);
	}

	/**
	 * Synchronizes instrumentation model, correspondence model and runtime
	 * environment model with files.
	 */
	private void syncRemainingModels() {
		if (correspondenceModel == null) {
			correspondenceModel = CorrespondenceFactory.eINSTANCE.createCorrespondences();
		}

		FileBackedModelUtil.synchronize(instrumentationModel, instrumentationModelFile, InstrumentationModel.class);
		FileBackedModelUtil.synchronize(correspondenceModel, correspondenceModelFile, Correspondences.class);
		FileBackedModelUtil.synchronize(runtimeEnvironmentModel, runtimeEnvironmentModelFile,
				RuntimeEnvironmentModel.class);
	}

	/**
	 * Loads the instrumentation model, the runtime environment model and the
	 * correspondence model from files.
	 * 
	 * @param config the model configuration which contains the file paths of the
	 *               models
	 */
	private void buildRemainingModels(ModelConfiguration config) {
		instrumentationModelFile = new File(config.getInstrumentationModelPath());
		runtimeEnvironmentModelFile = new File(config.getRuntimeEnvironmentPath());
		correspondenceModelFile = new File(config.getCorrespondencePath());

		instrumentationModel = ModelUtil.readFromFile(instrumentationModelFile, InstrumentationModel.class);
		runtimeEnvironmentModel = ModelUtil.readFromFile(runtimeEnvironmentModelFile, RuntimeEnvironmentModel.class);
		correspondenceModel = ModelUtil.readFromFile(correspondenceModelFile, Correspondences.class);
	}

	/**
	 * Builds the file system PCM which is synchronized with the current models.
	 * 
	 * @param config the model configuration which contains the paths where the PCM
	 *               should be stored
	 */
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

	/**
	 * Removes all problems and reports the configuration component as working.
	 */
	private void reportConfigurationWorking() {
		super.removeAllProblems();
		super.updateState();
		super.sendStateMessage(HealthStateObservedComponent.VSUM_MANAGER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InstrumentationModel getInstrumentation() {
		return architectureModel != null ? instrumentationModel : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RuntimeEnvironmentModel getRuntimeEnvironment() {
		return architectureModel != null ? runtimeEnvironmentModel : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Correspondences getCorrespondences() {
		return architectureModel != null ? correspondenceModel : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Repository getRepository() {
		return architectureModel != null ? architectureModel.getRepository() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public System getSystem() {
		return architectureModel != null ? architectureModel.getSystem() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResourceEnvironment getResourceEnvironment() {
		return architectureModel != null ? architectureModel.getResourceEnvironmentModel() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Allocation getAllocation() {
		return architectureModel != null ? architectureModel.getAllocationModel() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UsageModel getUsage() {
		return architectureModel != null ? architectureModel.getUsageModel() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InMemoryPCM getRaw() {
		return architectureModel;
	}

}
