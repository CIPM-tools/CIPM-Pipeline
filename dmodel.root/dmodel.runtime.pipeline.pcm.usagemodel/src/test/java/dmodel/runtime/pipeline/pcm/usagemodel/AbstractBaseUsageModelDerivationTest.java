package dmodel.runtime.pipeline.pcm.usagemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
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

import dmodel.base.core.IPcmModelProvider;
import dmodel.base.shared.ModelUtil;
import dmodel.base.shared.structure.Tree;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.runtime.pipeline.AbstractPipelineTestBase;
import dmodel.runtime.pipeline.BasePipelineTestConfiguration;
import dmodel.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.runtime.pipelinepcm.usagemodel.transformation.UsageDataDerivation;

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
		INIT_REPOSITORY = ModelUtil.readFromFile(new File("test-data/models/test.repository").getAbsolutePath(),
				Repository.class);

		INIT_SYSTEM = ModelUtil.readFromFile(new File("test-data/models/test.system").getAbsolutePath(), System.class);

		INIT_RESENV = ModelUtil.readFromFile(new File("test-data/models/test.resourceenvironment").getAbsolutePath(),
				ResourceEnvironment.class);

		INIT_ALLOCATION = ModelUtil.readFromFile(new File("test-data/models/test.allocation").getAbsolutePath(),
				Allocation.class);
	}

	@Before
	public void loadModelsAndVsum() {
		loadModels();
		super.reloadVsum();
	}

	protected void loadModels() {
		super.setSpecific(null, null, null);
		super.setPcm(
				ModelUtil.readFromFile(new File("test-data/models/test.repository").getAbsolutePath(),
						Repository.class),
				ModelUtil.readFromFile(new File("test-data/models/test.system").getAbsolutePath(), System.class),
				ModelUtil.readFromFile(new File("test-data/models/test.resourceenvironment").getAbsolutePath(),
						ResourceEnvironment.class),
				ModelUtil.readFromFile(new File("test-data/models/test.allocation").getAbsolutePath(),
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
