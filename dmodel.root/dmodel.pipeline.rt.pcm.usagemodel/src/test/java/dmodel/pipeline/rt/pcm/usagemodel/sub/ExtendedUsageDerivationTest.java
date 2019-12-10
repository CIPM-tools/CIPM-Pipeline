package dmodel.pipeline.rt.pcm.usagemodel.sub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.beust.jcommander.internal.Lists;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.UsageModelDerivationBaseTest;
import dmodel.pipeline.shared.structure.Tree;

public class ExtendedUsageDerivationTest extends UsageModelDerivationBaseTest {

	@Test
	public void oneScenarioTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/scenario1.dat");
		assertEquals(records.size(), 3);

		derivation.deriveUsageData(records, blackboard.getArchitectureModel(), Lists.newArrayList());

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_SYSTEM, blackboard.getArchitectureModel().getSystem()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));
		assertTrue(modelsEqual(INIT_ALLOCATION, blackboard.getArchitectureModel().getAllocationModel()));

		assertNotNull(blackboard.getArchitectureModel().getUsageModel());
		assertEquals(1, blackboard.getArchitectureModel().getUsageModel().getUsageScenario_UsageModel().size());
		assertNotNull(blackboard.getArchitectureModel().getUsageModel().getUsageScenario_UsageModel().get(0)
				.getScenarioBehaviour_UsageScenario());
		assertEquals(3, blackboard.getArchitectureModel().getUsageModel().getUsageScenario_UsageModel().get(0)
				.getScenarioBehaviour_UsageScenario().getActions_ScenarioBehaviour().size());
	}

	@Test
	public void oneScenarioTwoUserTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/scenario2.dat");
		assertEquals(records.size(), 6);

		derivation.deriveUsageData(records, blackboard.getArchitectureModel(), Lists.newArrayList());

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_SYSTEM, blackboard.getArchitectureModel().getSystem()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));
		assertTrue(modelsEqual(INIT_ALLOCATION, blackboard.getArchitectureModel().getAllocationModel()));

		assertNotNull(blackboard.getArchitectureModel().getUsageModel());
		assertEquals(2, blackboard.getArchitectureModel().getUsageModel().getUsageScenario_UsageModel().size());
		assertNotNull(blackboard.getArchitectureModel().getUsageModel().getUsageScenario_UsageModel().get(0)
				.getScenarioBehaviour_UsageScenario());
		assertEquals(3, blackboard.getArchitectureModel().getUsageModel().getUsageScenario_UsageModel().get(0)
				.getScenarioBehaviour_UsageScenario().getActions_ScenarioBehaviour().size());
	}

}
