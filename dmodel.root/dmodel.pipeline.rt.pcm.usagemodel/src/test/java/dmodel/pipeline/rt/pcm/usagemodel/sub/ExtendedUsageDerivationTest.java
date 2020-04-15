package dmodel.pipeline.rt.pcm.usagemodel.sub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.AbstractBaseUsageModelDerivationTest;
import dmodel.pipeline.shared.structure.Tree;

@RunWith(SpringRunner.class)
@Import(AbstractBaseUsageModelDerivationTest.UsageTransformationTestConfiguration.class)
public class ExtendedUsageDerivationTest extends AbstractBaseUsageModelDerivationTest {

	@Test
	public void oneScenarioTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/scenario1.dat");
		assertEquals(records.size(), 3);

		super.deriveUsageData(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));
		assertTrue(modelsEqual(INIT_ALLOCATION, pcm.getAllocation()));

		assertNotNull(pcm.getUsage());
		assertEquals(1, pcm.getUsage().getUsageScenario_UsageModel().size());
		assertNotNull(pcm.getUsage().getUsageScenario_UsageModel().get(0).getScenarioBehaviour_UsageScenario());
		assertEquals(5, pcm.getUsage().getUsageScenario_UsageModel().get(0).getScenarioBehaviour_UsageScenario()
				.getActions_ScenarioBehaviour().size());
	}

	@Test
	public void oneScenarioTwoUserTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/scenario2.dat");
		assertEquals(records.size(), 6);

		super.deriveUsageData(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));
		assertTrue(modelsEqual(INIT_ALLOCATION, pcm.getAllocation()));

		assertNotNull(pcm.getUsage());
		assertEquals(1, pcm.getUsage().getUsageScenario_UsageModel().size());
		assertNotNull(pcm.getUsage().getUsageScenario_UsageModel().get(0).getScenarioBehaviour_UsageScenario());
		assertEquals(5, pcm.getUsage().getUsageScenario_UsageModel().get(0).getScenarioBehaviour_UsageScenario()
				.getActions_ScenarioBehaviour().size());
	}

}
