package dmodel.pipeline.shared.pcm;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationFactory;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.system.SystemFactory;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import dmodel.pipeline.shared.FileBackedModelUtil;
import dmodel.pipeline.shared.ModelUtil;
import lombok.Data;

@Data
public class InMemoryPCM {
	private Repository repository;
	private System system;
	private UsageModel usageModel;
	private Allocation allocationModel;
	private ResourceEnvironment resourceEnvironmentModel;

	private long lastUpdatedRepository;
	private long lastUpdatedSystem;
	private long lastUpdatedUsage;
	private long lastUpdatedAllocation;
	private long lastUpdatedResourceEnv;

	private LocalFilesystemPCM reflected;

	public InMemoryPCM() {
		this.updatedAllocation();
		this.updatedRepository();
		this.updatedResourceEnv();
		this.updatedSystem();
		this.updatedUsage();
	}

	public static InMemoryPCM createFromFilesystemSynced(LocalFilesystemPCM pcm) {
		InMemoryPCM ret = createFromFilesystem(pcm);

		FileBackedModelUtil.synchronize(ret.getAllocationModel(), pcm.getAllocationModelFile(), Allocation.class,
				n -> ret.updatedAllocation(), null);
		FileBackedModelUtil.synchronize(ret.getRepository(), pcm.getRepositoryFile(), Repository.class,
				n -> ret.updatedRepository(), null);
		FileBackedModelUtil.synchronize(ret.getResourceEnvironmentModel(), pcm.getResourceEnvironmentFile(),
				ResourceEnvironment.class, n -> ret.updatedResourceEnv(), null);
		FileBackedModelUtil.synchronize(ret.getSystem(), pcm.getSystemFile(), System.class, n -> ret.updatedSystem(),
				null);
		FileBackedModelUtil.synchronize(ret.getUsageModel(), pcm.getUsageModelFile(), UsageModel.class,
				n -> ret.updatedUsage(), null);
		ret.reflected = pcm;

		return ret;
	}

	/**
	 * Loads the palladio models from the files. If a file does not exist, this
	 * method initializes an empty model.
	 * 
	 * @param pcm file backed palladio models
	 * @return loaded palladio models (guarantees that all parts are not null)
	 */
	public static InMemoryPCM createFromFilesystem(LocalFilesystemPCM pcm) {
		InMemoryPCM ret = new InMemoryPCM();
		ret.reflected = pcm;

		// empty models if not available
		if (pcm.getRepositoryFile().exists()) {
			ret.repository = ModelUtil.readFromFile(pcm.getRepositoryFile().getAbsolutePath(), Repository.class);
		} else {
			ret.repository = RepositoryFactory.eINSTANCE.createRepository();
		}

		if (pcm.getAllocationModelFile().exists()) {
			ret.allocationModel = ModelUtil.readFromFile(pcm.getAllocationModelFile().getAbsolutePath(),
					Allocation.class);
		} else {
			ret.allocationModel = AllocationFactory.eINSTANCE.createAllocation();
		}

		if (pcm.getResourceEnvironmentFile().exists()) {
			ret.resourceEnvironmentModel = ModelUtil.readFromFile(pcm.getResourceEnvironmentFile().getAbsolutePath(),
					ResourceEnvironment.class);
		} else {
			ret.resourceEnvironmentModel = ResourceenvironmentFactory.eINSTANCE.createResourceEnvironment();
		}

		if (pcm.getSystemFile().exists()) {
			ret.system = ModelUtil.readFromFile(pcm.getSystemFile().getAbsolutePath(), System.class);
		} else {
			ret.system = SystemFactory.eINSTANCE.createSystem();
		}

		if (pcm.getUsageModelFile().exists()) {
			ret.usageModel = ModelUtil.readFromFile(pcm.getUsageModelFile().getAbsolutePath(), UsageModel.class);
		} else {
			ret.usageModel = UsagemodelFactory.eINSTANCE.createUsageModel();
		}

		return ret;
	}

	public void saveToFilesystem(LocalFilesystemPCM pcm) {
		ModelUtil.saveToFile(this.getRepository(), pcm.getRepositoryFile());
		ModelUtil.saveToFile(this.getAllocationModel(), pcm.getAllocationModelFile());
		ModelUtil.saveToFile(this.getResourceEnvironmentModel(), pcm.getResourceEnvironmentFile());
		ModelUtil.saveToFile(this.getSystem(), pcm.getSystemFile());
		ModelUtil.saveToFile(this.getUsageModel(), pcm.getUsageModelFile());
	}

	private void updatedRepository() {
		this.lastUpdatedRepository = java.lang.System.currentTimeMillis();
	}

	private void updatedSystem() {
		this.lastUpdatedSystem = java.lang.System.currentTimeMillis();
	}

	private void updatedUsage() {
		this.lastUpdatedUsage = java.lang.System.currentTimeMillis();
	}

	private void updatedAllocation() {
		this.lastUpdatedAllocation = java.lang.System.currentTimeMillis();
	}

	private void updatedResourceEnv() {
		this.lastUpdatedResourceEnv = java.lang.System.currentTimeMillis();
	}

	public void swapSystem(System currentSystem) {
		FileBackedModelUtil.clear(this.system);
		if (reflected != null) {
			this.system = FileBackedModelUtil.synchronize(currentSystem, reflected.getSystemFile(), System.class,
					n -> updatedSystem(), null);
			this.updatedSystem();
		} else {
			this.system = currentSystem;
			this.updatedSystem();
		}
	}

	public void clearListeners() {
		FileBackedModelUtil.clear(allocationModel);
		FileBackedModelUtil.clear(repository);
		FileBackedModelUtil.clear(resourceEnvironmentModel);
		FileBackedModelUtil.clear(usageModel);
		FileBackedModelUtil.clear(system);
	}
}