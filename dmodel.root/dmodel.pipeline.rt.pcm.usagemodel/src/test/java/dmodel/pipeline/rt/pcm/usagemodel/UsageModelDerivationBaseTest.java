package dmodel.pipeline.rt.pcm.usagemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;

import com.beust.jcommander.internal.Lists;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.transformation.UsageDataDerivation;
import dmodel.pipeline.rt.pipeline.AbstractTransformationTest;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.structure.Tree;

public class UsageModelDerivationBaseTest extends AbstractTransformationTest {
	protected static Repository INIT_REPOSITORY;
	protected static System INIT_SYSTEM;
	protected static ResourceEnvironment INIT_RESENV;
	protected static Allocation INIT_ALLOCATION;

	protected UsageDataDerivation derivation;

	@Before
	public void initTransformation() {
		derivation = new UsageDataDerivation();
	}

	@BeforeClass
	public static void loadInitModels() {
		INIT_REPOSITORY = ModelUtil.readFromResource(
				UsageModelDerivationBaseTest.class.getResource("/models/test.repository"), Repository.class);

		INIT_SYSTEM = ModelUtil.readFromResource(UsageModelDerivationBaseTest.class.getResource("/models/test.system"),
				org.palladiosimulator.pcm.system.System.class);

		INIT_RESENV = ModelUtil.readFromResource(
				UsageModelDerivationBaseTest.class.getResource("/models/test.resourceenvironment"),
				ResourceEnvironment.class);

		INIT_ALLOCATION = ModelUtil.readFromResource(
				UsageModelDerivationBaseTest.class.getResource("/models/test.allocation"), Allocation.class);
	}

	@Override
	protected void loadPCMModels() {
		blackboard.getArchitectureModel().setRepository(ModelUtil.readFromResource(
				UsageModelDerivationBaseTest.class.getResource("/models/test.repository"), Repository.class));

		blackboard.getArchitectureModel()
				.setSystem(ModelUtil.readFromResource(
						UsageModelDerivationBaseTest.class.getResource("/models/test.system"),
						org.palladiosimulator.pcm.system.System.class));

		blackboard.getArchitectureModel()
				.setResourceEnvironmentModel(ModelUtil.readFromResource(
						UsageModelDerivationBaseTest.class.getResource("/models/test.resourceenvironment"),
						ResourceEnvironment.class));

		blackboard.getArchitectureModel().setAllocationModel(ModelUtil.readFromResource(
				UsageModelDerivationBaseTest.class.getResource("/models/test.allocation"), Allocation.class));
	}

	@Test
	public void noMonitoringDataTest() {
		List<Tree<ServiceCallRecord>> records = Lists.newArrayList();

		derivation.deriveUsageData(records, blackboard.getArchitectureModel(), null);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_SYSTEM, blackboard.getArchitectureModel().getSystem()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));
		assertTrue(modelsEqual(INIT_ALLOCATION, blackboard.getArchitectureModel().getAllocationModel()));

		assertNotNull(blackboard.getArchitectureModel().getUsageModel());
		assertEquals(0, blackboard.getArchitectureModel().getUsageModel().getUsageScenario_UsageModel().size());
	}

}
