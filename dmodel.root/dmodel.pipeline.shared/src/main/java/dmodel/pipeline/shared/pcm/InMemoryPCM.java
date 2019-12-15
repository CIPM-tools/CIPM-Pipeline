package dmodel.pipeline.shared.pcm;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.allocation.AllocationFactory;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.system.SystemFactory;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;
import org.pcm.headless.api.client.transform.TransitiveModelTransformerUtil;

import com.google.common.collect.Lists;

import dmodel.pipeline.shared.FileBackedModelUtil;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
import lombok.Builder;
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

	@Builder
	public InMemoryPCM(Repository repository, System system, UsageModel usageModel, Allocation allocationModel,
			ResourceEnvironment resourceEnvironmentModel) {
		this.repository = repository;
		this.system = system;
		this.usageModel = usageModel;
		this.allocationModel = allocationModel;
		this.resourceEnvironmentModel = resourceEnvironmentModel;

		this.updatedAllocation();
		this.updatedRepository();
		this.updatedResourceEnv();
		this.updatedSystem();
		this.updatedUsage();
	}

	public InMemoryPCM() {
		this.updatedAllocation();
		this.updatedRepository();
		this.updatedResourceEnv();
		this.updatedSystem();
		this.updatedUsage();
	}

	public void syncWithFilesystem(LocalFilesystemPCM pcm) {
		FileBackedModelUtil.synchronize(this.getAllocationModel(), pcm.getAllocationModelFile(), Allocation.class,
				n -> this.updatedAllocation(), null);
		FileBackedModelUtil.synchronize(this.getRepository(), pcm.getRepositoryFile(), Repository.class,
				n -> this.updatedRepository(), null);
		FileBackedModelUtil.synchronize(this.getResourceEnvironmentModel(), pcm.getResourceEnvironmentFile(),
				ResourceEnvironment.class, n -> this.updatedResourceEnv(), null);
		FileBackedModelUtil.synchronize(this.getSystem(), pcm.getSystemFile(), System.class, n -> this.updatedSystem(),
				null);
		FileBackedModelUtil.synchronize(this.getUsageModel(), pcm.getUsageModelFile(), UsageModel.class,
				n -> this.updatedUsage(), null);
		this.reflected = pcm;
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

		if (pcm.getAllocationModelFile().exists()) {
			ret.allocationModel = ModelUtil.readFromFile(pcm.getAllocationModelFile().getAbsolutePath(),
					Allocation.class);
		} else {
			ret.allocationModel = AllocationFactory.eINSTANCE.createAllocation();
			ret.allocationModel.setSystem_Allocation(ret.system);
			ret.allocationModel.setTargetResourceEnvironment_Allocation(ret.resourceEnvironmentModel);
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

	public void swapRepository(Repository other) {
		FileBackedModelUtil.clear(this.repository);
		if (reflected != null) {
			this.repository = FileBackedModelUtil.synchronize(other, reflected.getRepositoryFile(), Repository.class,
					n -> updatedRepository(), null);
			this.updatedRepository();
		} else {
			this.repository = other;
			this.updatedRepository();
		}
	}

	public void swapUsageModel(UsageModel other) {
		FileBackedModelUtil.clear(this.system);
		if (reflected != null) {
			this.usageModel = FileBackedModelUtil.synchronize(other, reflected.getUsageModelFile(), UsageModel.class,
					n -> updatedUsage(), null);
			this.updatedSystem();
		} else {
			this.usageModel = other;
			this.updatedSystem();
		}
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

		// update link to system
		this.allocationModel.setSystem_Allocation(this.system);
		Set<String> containedIds = PCMUtils.getElementsByType(this.system, AssemblyContext.class).stream()
				.map(ac -> ac.getId()).collect(Collectors.toSet());
		List<AllocationContext> toRemoveCtxs = PCMUtils.getElementsByType(this.allocationModel, AllocationContext.class)
				.stream().filter(ac -> !containedIds.contains(ac.getId())).collect(Collectors.toList());
		toRemoveCtxs.forEach(ac -> this.allocationModel.getAllocationContexts_Allocation().remove(ac));
	}

	public void clearListeners() {
		FileBackedModelUtil.clear(allocationModel);
		FileBackedModelUtil.clear(repository);
		FileBackedModelUtil.clear(resourceEnvironmentModel);
		FileBackedModelUtil.clear(usageModel);
		FileBackedModelUtil.clear(system);
	}

	public InMemoryPCM copyReference() {
		return InMemoryPCM.builder().repository(getRepository()).system(getSystem())
				.resourceEnvironmentModel(getResourceEnvironmentModel()).allocationModel(getAllocationModel())
				.usageModel(getUsageModel()).build();
	}

	public InMemoryPCM copyDeep() {
		TransitiveModelTransformerUtil transformer = new TransitiveModelTransformerUtil();
		List<EObject> orgs = Lists.newArrayList(allocationModel, repository, resourceEnvironmentModel, usageModel,
				system);
		List<EObject> copies = transformer.copyObjects(orgs);

		Repository repo = copies.stream().filter(f -> f instanceof Repository).map(Repository.class::cast).findFirst()
				.orElse(null);
		System system = copies.stream().filter(f -> f instanceof System).map(System.class::cast).findFirst()
				.orElse(null);
		ResourceEnvironment env = copies.stream().filter(f -> f instanceof ResourceEnvironment)
				.map(ResourceEnvironment.class::cast).findFirst().orElse(null);
		Allocation alloc = copies.stream().filter(f -> f instanceof Allocation).map(Allocation.class::cast).findFirst()
				.orElse(null);
		UsageModel usage = copies.stream().filter(f -> f instanceof UsageModel).map(UsageModel.class::cast).findFirst()
				.orElse(null);

		return InMemoryPCM.builder().repository(repo).system(system).resourceEnvironmentModel(env)
				.allocationModel(alloc).usageModel(usage).build();
	}
}