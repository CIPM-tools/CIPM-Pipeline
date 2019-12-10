package dmodel.pipeline.rt.pcm.system.sub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

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

	@Override
	protected void loadPCMModels() {
		blackboard.getArchitectureModel().setRepository(ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test.repository"), Repository.class));

		blackboard.getArchitectureModel()
				.setSystem(ModelUtil.readFromResource(
						AbstractBaseSystemTransformationTest.class.getResource("/models/test.system"),
						org.palladiosimulator.pcm.system.System.class));

		blackboard.getArchitectureModel()
				.setResourceEnvironmentModel(ModelUtil.readFromResource(
						AbstractBaseSystemTransformationTest.class.getResource("/models/test.resourceenvironment"),
						ResourceEnvironment.class));

		blackboard.getArchitectureModel().setAllocationModel(ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test.allocation"), Allocation.class));
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
	public void deprecationTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/dbchange1.dat");
		List<Tree<ServiceCallRecord>> records2 = Lists.newArrayList();

		transformation.deriveSystemData(records);
		transformation.deriveSystemData(records2);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));

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
		assertEquals(0, countAssembly(blackboard.getArchitectureModel().getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));

		AssemblyContext aLogic = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ")
				.get(0);
		AssemblyContext aDB2 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ")
				.get(0);

		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));

	}

	@Test
	public void dbChangeTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/dbchange1.dat");
		assertEquals(records.size(), 2);

		transformation.deriveSystemData(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));

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

	@Test
	public void changeBackTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/changeback1.dat");
		assertEquals(records.size(), 3);

		transformation.deriveSystemData(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));

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

		assertFalse(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB1,
				aDB1.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
	}

	@Test
	public void stackedChangesTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/changeback1.dat");
		List<Tree<ServiceCallRecord>> records2 = parseMonitoringResource("/monitoring/dbchange1.dat");

		transformation.deriveSystemData(records);
		transformation.deriveSystemData(records2);

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
