package dmodel.pipeline.core;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.pipeline.core.facade.pcm.IAllocationQueryFacade;
import dmodel.pipeline.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.pipeline.core.facade.pcm.ISystemQueryFacade;
import dmodel.pipeline.core.facade.pcm.impl.AllocationQueryFacadeImpl;
import dmodel.pipeline.core.facade.pcm.impl.RepositoryQueryFacadeImpl;
import dmodel.pipeline.core.facade.pcm.impl.SystemQueryFacadeImpl;
import dmodel.pipeline.core.health.HealthState;
import dmodel.pipeline.core.health.HealthStateManager;
import dmodel.pipeline.core.health.HealthStateObservedComponent;
import dmodel.pipeline.shared.correspondence.CorrespondenceUtil;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pcm.util.PCMUtils;

@RunWith(SpringRunner.class)
public class AbstractCoreTest {

	@TestConfiguration
	public static class CoreContextConfiguration {

		@Bean
		public StaticModelProviderImpl provideModels() {
			return new StaticModelProviderImpl();
		}

		@Bean
		public IRepositoryQueryFacade repoFacade() {
			return new RepositoryQueryFacadeImpl();
		}

		@Bean
		public ISystemQueryFacade systemFacade() {
			return new SystemQueryFacadeImpl();
		}

		@Bean
		public IAllocationQueryFacade allocationFacade() {
			return new AllocationQueryFacadeImpl();
		}

		@Bean
		public HealthStateManager healthStateManager() {
			return new HealthStateManager();
		}

	}

	@BeforeClass
	public static void initModels() {
		PCMUtils.loadPCMModels();
		CorrespondenceUtil.initVitruv();
	}

	@Autowired
	private StaticModelProviderImpl modelProvider;

	@Autowired
	private HealthStateManager healthStateManager;

	@Before
	public void reportAllWorking() {
		for (HealthStateObservedComponent comp : HealthStateObservedComponent.values()) {
			healthStateManager.update(comp, HealthState.WORKING);
		}
	}

	protected void setPcm(Repository repository, System system, ResourceEnvironment env, Allocation alloc,
			UsageModel usage) {
		repository = repository == null ? RepositoryFactory.eINSTANCE.createRepository() : repository;
		system = system == null ? SystemFactory.eINSTANCE.createSystem() : system;
		env = env == null ? ResourceenvironmentFactory.eINSTANCE.createResourceEnvironment() : env;
		alloc = alloc == null ? AllocationFactory.eINSTANCE.createAllocation() : alloc;
		usage = usage == null ? UsagemodelFactory.eINSTANCE.createUsageModel() : usage;

		modelProvider.setPCM(InMemoryPCM.builder().repository(repository).system(system).resourceEnvironmentModel(env)
				.allocationModel(alloc).usageModel(usage).build());
	}

}
