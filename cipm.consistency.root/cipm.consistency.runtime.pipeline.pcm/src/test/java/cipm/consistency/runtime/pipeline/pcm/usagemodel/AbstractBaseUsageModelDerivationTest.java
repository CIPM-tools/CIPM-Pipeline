package cipm.consistency.runtime.pipeline.pcm.usagemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.beust.jcommander.internal.Lists;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.runtime.pipeline.AbstractPipelineTestBase;
import cipm.consistency.runtime.pipeline.BasePipelineTestConfiguration;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.transformation.UsageDataDerivation;

@RunWith(SpringRunner.class)
public abstract class AbstractBaseUsageModelDerivationTest extends AbstractPipelineTestBase {
	protected static Repository INIT_REPOSITORY;
	protected static System INIT_SYSTEM;
	protected static ResourceEnvironment INIT_RESENV;
	protected static Allocation INIT_ALLOCATION;

	@Autowired
	protected UsageDataDerivation derivation;

	@Autowired
	protected RuntimePipelineBlackboard blackboard;

	@Autowired
	protected IPcmModelProvider pcm;

	@TestConfiguration
	public static class UsageTransformationTestConfiguration
			extends BasePipelineTestConfiguration.TestContextConfiguration {

		@Bean
		public UsageDataDerivation transformation() {
			return new UsageDataDerivation();
		}

	}

	@BeforeClass
	public static void loadInitModels() {
		INIT_REPOSITORY = ModelUtil.readFromResource(
				AbstractBaseUsageModelDerivationTest.class.getResource("/models/test.repository"), Repository.class);

		INIT_SYSTEM = ModelUtil.readFromResource(
				AbstractBaseUsageModelDerivationTest.class.getResource("/models/test.system"), System.class);

		INIT_RESENV = ModelUtil.readFromResource(
				AbstractBaseUsageModelDerivationTest.class.getResource("/models/test.resourceenvironment"),
				ResourceEnvironment.class);

		INIT_ALLOCATION = ModelUtil.readFromResource(
				AbstractBaseUsageModelDerivationTest.class.getResource("/models/test.allocation"), Allocation.class);
	}

	@Before
	public void loadModelsAndVsum() {
		loadModels();
		super.reloadVsum();
	}

	protected void loadModels() {
		super.setSpecific(null, null, null);
		super.setPcm(ModelUtil.readFromResource(
				AbstractBaseUsageModelDerivationTest.class.getResource("/models/test.repository"), Repository.class),
				ModelUtil.readFromResource(
						AbstractBaseUsageModelDerivationTest.class.getResource("/models/test.system"), System.class),
				ModelUtil.readFromResource(
						AbstractBaseUsageModelDerivationTest.class.getResource("/models/test.resourceenvironment"),
						ResourceEnvironment.class),
				ModelUtil.readFromResource(
						AbstractBaseUsageModelDerivationTest.class.getResource("/models/test.allocation"),
						Allocation.class),
				null);
	}

	protected void deriveUsageData(List<Tree<ServiceCallRecord>> records) {
		List<UsageScenario> scenarios = derivation.deriveUsageData(records, blackboard.getPcmQuery(), null);
		pcm.getUsage().getUsageScenario_UsageModel().clear();
		pcm.getUsage().getUsageScenario_UsageModel().addAll(scenarios);
	}

	@Test
	public void noMonitoringDataTest() {
		List<Tree<ServiceCallRecord>> records = Lists.newArrayList();

		derivation.deriveUsageData(records, blackboard.getPcmQuery(), null);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));
		assertTrue(modelsEqual(INIT_ALLOCATION, pcm.getAllocation()));

		assertNotNull(pcm.getUsage());
		assertEquals(0, pcm.getUsage().getUsageScenario_UsageModel().size());
	}

}
