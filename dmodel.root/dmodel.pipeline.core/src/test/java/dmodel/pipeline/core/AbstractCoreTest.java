package dmodel.pipeline.core;

import java.io.IOException;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import dmodel.pipeline.core.config.ConfigurationContainer;
import dmodel.pipeline.core.facade.IPCMQueryFacade;
import dmodel.pipeline.core.facade.impl.PcmQueryImpl;
import dmodel.pipeline.core.facade.pcm.IAllocationQueryFacade;
import dmodel.pipeline.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.pipeline.core.facade.pcm.IResourceEnvironmentQueryFacade;
import dmodel.pipeline.core.facade.pcm.ISystemQueryFacade;
import dmodel.pipeline.core.facade.pcm.IUsageQueryFacade;
import dmodel.pipeline.core.facade.pcm.impl.AllocationQueryFacadeImpl;
import dmodel.pipeline.core.facade.pcm.impl.RepositoryQueryFacadeImpl;
import dmodel.pipeline.core.facade.pcm.impl.SystemQueryFacadeImpl;
import dmodel.pipeline.core.facade.pcm.impl.UsageQueryFacadeImpl;
import dmodel.pipeline.core.health.HealthState;
import dmodel.pipeline.core.health.HealthStateManager;
import dmodel.pipeline.core.health.HealthStateObservedComponent;
import dmodel.pipeline.core.mocks.HealthStateMessageSender;
import dmodel.pipeline.core.mocks.ResourceEnvironmentFacadeMockImpl;
import dmodel.pipeline.core.mocks.StaticModelProviderImpl;
import dmodel.pipeline.core.mocks.StaticSpecificModelProviderImpl;
import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import dmodel.pipeline.shared.correspondence.CorrespondenceUtil;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
import tools.vitruv.framework.correspondence.Correspondences;

@RunWith(SpringRunner.class)
public abstract class AbstractCoreTest {

	@TestConfiguration
	public static class CoreContextConfiguration {

		@Bean
		public IPCMQueryFacade pcmFacade() {
			return new PcmQueryImpl();
		}

		@Bean
		public StaticModelProviderImpl provideModels() {
			return new StaticModelProviderImpl();
		}

		@Bean
		public StaticSpecificModelProviderImpl provideSpecificModels() {
			return new StaticSpecificModelProviderImpl();
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
		public IUsageQueryFacade queryFacade() {
			return new UsageQueryFacadeImpl();
		}

		@Bean
		public IResourceEnvironmentQueryFacade envFacade() {
			return new ResourceEnvironmentFacadeMockImpl();
		}

		@Bean
		public HealthStateManager healthStateManager() {
			return new HealthStateManager();
		}

		@Bean
		public HealthStateMessageSender healthStateMsgSender() {
			return new HealthStateMessageSender();
		}

		@Bean
		public ConfigurationContainer configuration() {
			try {
				return new ObjectMapper(new YAMLFactory()).readValue(
						AbstractCoreTest.class.getResourceAsStream("/defaultConfig.yml"), ConfigurationContainer.class);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
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
	private StaticSpecificModelProviderImpl specificModelProvider;

	@Autowired
	private HealthStateManager healthStateManager;

	@Before
	public void reportAllWorking() {
		for (HealthStateObservedComponent comp : HealthStateObservedComponent.values()) {
			healthStateManager.update(comp, HealthState.WORKING);
		}
	}

	protected void setSpecific(RuntimeEnvironmentModel rem, InstrumentationModel inm, Correspondences cps) {
		specificModelProvider.setModels(inm, rem, cps);
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
