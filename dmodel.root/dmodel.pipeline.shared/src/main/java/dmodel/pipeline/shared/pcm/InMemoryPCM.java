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

import dmodel.pipeline.shared.ModelUtil;

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

	public InMemoryPCM() {
		this.updatedAllocation();
		this.updatedRepository();
		this.updatedResourceEnv();
		this.updatedSystem();
		this.updatedUsage();
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
		ModelUtil.saveToFile(this.getRepository(), pcm.getRepositoryFile().getAbsolutePath());
		ModelUtil.saveToFile(this.getAllocationModel(), pcm.getAllocationModelFile().getAbsolutePath());
		ModelUtil.saveToFile(this.getResourceEnvironmentModel(), pcm.getResourceEnvironmentFile().getAbsolutePath());
		ModelUtil.saveToFile(this.getSystem(), pcm.getSystemFile().getAbsolutePath());
		ModelUtil.saveToFile(this.getUsageModel(), pcm.getUsageModelFile().getAbsolutePath());
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}

	public UsageModel getUsageModel() {
		return usageModel;
	}

	public void setUsageModel(UsageModel usageModel) {
		this.usageModel = usageModel;
	}

	public Allocation getAllocationModel() {
		return allocationModel;
	}

	public void setAllocationModel(Allocation allocationModel) {
		this.allocationModel = allocationModel;
	}

	public ResourceEnvironment getResourceEnvironmentModel() {
		return resourceEnvironmentModel;
	}

	public void setResourceEnvironmentModel(ResourceEnvironment resourceEnvironmentModel) {
		this.resourceEnvironmentModel = resourceEnvironmentModel;
	}

	public void updatedRepository() {
		this.lastUpdatedRepository = java.lang.System.currentTimeMillis();
	}

	public void updatedSystem() {
		this.lastUpdatedSystem = java.lang.System.currentTimeMillis();
	}

	public void updatedUsage() {
		this.lastUpdatedUsage = java.lang.System.currentTimeMillis();
	}

	public void updatedAllocation() {
		this.lastUpdatedAllocation = java.lang.System.currentTimeMillis();
	}

	public void updatedResourceEnv() {
		this.lastUpdatedResourceEnv = java.lang.System.currentTimeMillis();
	}

	public long getLastUpdatedRepository() {
		return lastUpdatedRepository;
	}

	public long getLastUpdatedSystem() {
		return lastUpdatedSystem;
	}

	public long getLastUpdatedUsage() {
		return lastUpdatedUsage;
	}

	public long getLastUpdatedAllocation() {
		return lastUpdatedAllocation;
	}

	public long getLastUpdatedResourceEnv() {
		return lastUpdatedResourceEnv;
	}
}