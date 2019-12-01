package dmodel.pipeline.rt.pcm.system.sub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;

import com.beust.jcommander.internal.Lists;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.system.AbstractBaseSystemTransformationTest;
import dmodel.pipeline.rt.pcm.system.RuntimeSystemDerivation;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.structure.Tree;

public class SimpleSystemTransformationTest extends AbstractBaseSystemTransformationTest {

	private RuntimeSystemDerivation transformation;

	@Before
	public void initTransformation() {
		transformation = new RuntimeSystemDerivation();
		transformation.setBlackboard(blackboard);
	}

	@Test
	public void noMonitoringDataTest() {
		List<Tree<ServiceCallRecord>> records = Lists.newArrayList();

		transformation.deriveSystemData(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_SYSTEM, blackboard.getArchitectureModel().getSystem()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));
		assertTrue(modelsEqual(INIT_ALLOCATION, blackboard.getArchitectureModel().getAllocationModel()));
	}

	@Test
	public void noModificationTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/nomodification.dat");
		assertEquals(records.size(), 3);

		transformation.deriveSystemData(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_SYSTEM, blackboard.getArchitectureModel().getSystem()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));
		assertTrue(modelsEqual(INIT_ALLOCATION, blackboard.getArchitectureModel().getAllocationModel()));
	}

	@Test
	public void dbChangeTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/dbchange1.dat");
		assertEquals(records.size(), 2);

		transformation.deriveSystemData(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));
		assertTrue(modelsEqual(INIT_ALLOCATION, blackboard.getArchitectureModel().getAllocationModel()));

		// system should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, blackboard.getArchitectureModel().getSystem()));
		assertEquals(blackboard.getArchitectureModel().getSystem().getConnectors__ComposedStructure().size(), 3);
		assertEquals(
				ModelUtil.getObjects(blackboard.getArchitectureModel().getSystem(), AssemblyConnector.class).size(), 2);
		assertEquals(ModelUtil
				.getObjects(blackboard.getArchitectureModel().getSystem(), ProvidedDelegationConnector.class).size(),
				1);

		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));

		AssemblyContext aLogic = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ")
				.get(0);
		AssemblyContext aDB1 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ")
				.get(0);
		AssemblyContext aDB2 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ")
				.get(0);

		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertFalse(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB1,
				aDB1.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));

	}

}
