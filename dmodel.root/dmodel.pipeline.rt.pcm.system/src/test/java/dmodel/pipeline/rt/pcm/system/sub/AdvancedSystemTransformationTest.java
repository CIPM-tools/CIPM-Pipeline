package dmodel.pipeline.rt.pcm.system.sub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
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

public class AdvancedSystemTransformationTest extends AbstractBaseSystemTransformationTest {

	private RuntimeSystemDerivation transformation;

	@Before
	public void initTransformation() {
		transformation = new RuntimeSystemDerivation();
		transformation.setBlackboard(blackboard);
	}

	@BeforeClass
	public static void overwriteDefaultModels() {
		INIT_ALLOCATION = ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test2.allocation"), Allocation.class);
	}

	@Override
	protected void loadPCMModels() {
		blackboard.getArchitectureModel().setRepository(ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test.repository"), Repository.class));

		blackboard.getArchitectureModel()
				.setSystem(ModelUtil.readFromResource(
						AbstractBaseSystemTransformationTest.class.getResource("/models/test2.system"),
						org.palladiosimulator.pcm.system.System.class));

		blackboard.getArchitectureModel()
				.setResourceEnvironmentModel(ModelUtil.readFromResource(
						AbstractBaseSystemTransformationTest.class.getResource("/models/test.resourceenvironment"),
						ResourceEnvironment.class));

		blackboard.getArchitectureModel().setAllocationModel(ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test2.allocation"), Allocation.class));
	}

	@Test
	public void fullEnvironmentChange() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/environmentchange.dat");
		assertEquals(records.size(), 2);

		transformation.deriveSystemData(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));

		// system & allocation should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, blackboard.getArchitectureModel().getSystem()));
		assertFalse(modelsEqual(INIT_ALLOCATION, blackboard.getArchitectureModel().getAllocationModel()));
	}

	@Test
	public void uiChangeTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/uichange.dat");
		assertEquals(records.size(), 3);

		transformation.deriveSystemData(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));

		// system should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, blackboard.getArchitectureModel().getSystem()));
		assertEquals(blackboard.getArchitectureModel().getSystem().getConnectors__ComposedStructure().size(), 5);
		assertEquals(
				ModelUtil.getObjects(blackboard.getArchitectureModel().getSystem(), AssemblyConnector.class).size(), 4);
		assertEquals(ModelUtil
				.getObjects(blackboard.getArchitectureModel().getSystem(), ProvidedDelegationConnector.class).size(),
				1);

		assertEquals(7, blackboard.getArchitectureModel().getSystem().getAssemblyContexts__ComposedStructure().size());
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(2, countAssembly(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_vFEekBXdEeqKY-U3QOe1UQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_lc4ZgBYjEeqKY-U3QOe1UQ"));

		AssemblyContext aLogic = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ")
				.get(0);
		AssemblyContext aUI2 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_lc4ZgBYjEeqKY-U3QOe1UQ")
				.get(0);
		AssemblyContext aDB1 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ")
				.get(0);
		AssemblyContext aDB2 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ")
				.get(1);
		AssemblyContext aTranslator1 = getAssemblys(blackboard.getArchitectureModel().getSystem(),
				"_vFEekBXdEeqKY-U3QOe1UQ").get(0);
		AssemblyContext aDB22 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ")
				.get(0);

		ProvidedDelegationConnector prov = ModelUtil
				.getObjects(blackboard.getArchitectureModel().getSystem(), ProvidedDelegationConnector.class).get(0);
		assertTrue(prov.getAssemblyContext_ProvidedDelegationConnector().getId().equals(aUI2.getId()));
		assertTrue(prov.getInnerProvidedRole_ProvidedDelegationConnector().getId()
				.equals(aUI2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity()
						.get(0).getId()));

		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aUI2,
				aUI2.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));

		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertFalse(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB1,
				aDB1.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(1),
				aTranslator1, aTranslator1.getEncapsulatedComponent__AssemblyContext()
						.getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aTranslator1,
				aTranslator1.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity()
						.get(0),
				aDB22,
				aDB22.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
	}

	@Test
	public void dbChangeTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/dbchange2.dat");
		assertEquals(records.size(), 2);

		transformation.deriveSystemData(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));

		// system should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, blackboard.getArchitectureModel().getSystem()));
		assertEquals(blackboard.getArchitectureModel().getSystem().getConnectors__ComposedStructure().size(), 5);
		assertEquals(
				ModelUtil.getObjects(blackboard.getArchitectureModel().getSystem(), AssemblyConnector.class).size(), 4);
		assertEquals(ModelUtil
				.getObjects(blackboard.getArchitectureModel().getSystem(), ProvidedDelegationConnector.class).size(),
				1);

		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(2, countAssembly(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_vFEekBXdEeqKY-U3QOe1UQ"));

		AssemblyContext aLogic = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ")
				.get(0);
		AssemblyContext aDB1 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ")
				.get(0);
		AssemblyContext aDB2 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ")
				.get(1);
		AssemblyContext aTranslator1 = getAssemblys(blackboard.getArchitectureModel().getSystem(),
				"_vFEekBXdEeqKY-U3QOe1UQ").get(0);
		AssemblyContext aDB22 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ")
				.get(0);

		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertFalse(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB1,
				aDB1.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(1),
				aTranslator1, aTranslator1.getEncapsulatedComponent__AssemblyContext()
						.getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aTranslator1,
				aTranslator1.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity()
						.get(0),
				aDB22,
				aDB22.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));

	}

	@Test
	public void translatorChangeTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/translatorchange.dat");
		assertEquals(records.size(), 2);

		transformation.deriveSystemData(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));

		// system should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, blackboard.getArchitectureModel().getSystem()));
		assertEquals(blackboard.getArchitectureModel().getSystem().getConnectors__ComposedStructure().size(), 5);
		assertEquals(
				ModelUtil.getObjects(blackboard.getArchitectureModel().getSystem(), AssemblyConnector.class).size(), 4);
		assertEquals(ModelUtil
				.getObjects(blackboard.getArchitectureModel().getSystem(), ProvidedDelegationConnector.class).size(),
				1);

		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(2, countAssembly(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_vFEekBXdEeqKY-U3QOe1UQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_RMCOwBXcEeqKY-U3QOe1UQ"));

		AssemblyContext aLogic = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ")
				.get(0);
		AssemblyContext aDB1 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ")
				.get(0);
		AssemblyContext aDB2 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ")
				.get(1);
		AssemblyContext aTranslator1 = getAssemblys(blackboard.getArchitectureModel().getSystem(),
				"_vFEekBXdEeqKY-U3QOe1UQ").get(0);
		AssemblyContext aTranslator2 = getAssemblys(blackboard.getArchitectureModel().getSystem(),
				"_RMCOwBXcEeqKY-U3QOe1UQ").get(0);
		AssemblyContext aDB22 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ")
				.get(0);

		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertFalse(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB1,
				aDB1.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertFalse(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(1),
				aTranslator1, aTranslator1.getEncapsulatedComponent__AssemblyContext()
						.getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aTranslator1,
				aTranslator1.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity()
						.get(0),
				aDB22,
				aDB22.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(1),
				aTranslator2, aTranslator2.getEncapsulatedComponent__AssemblyContext()
						.getProvidedRoles_InterfaceProvidingEntity().get(0)));

	}

	@Test
	public void translatorChangeDeprecatedTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/translatorchange.dat");
		List<Tree<ServiceCallRecord>> records2 = Lists.newArrayList();
		assertEquals(records.size(), 2);

		transformation.deriveSystemData(records);
		transformation.deriveSystemData(records2);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, blackboard.getArchitectureModel().getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, blackboard.getArchitectureModel().getResourceEnvironmentModel()));

		// ModelUtil.saveToFile(blackboard.getArchitectureModel().getSystem(),
		// "result.system");

		// system should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, blackboard.getArchitectureModel().getSystem()));
		assertEquals(blackboard.getArchitectureModel().getSystem().getConnectors__ComposedStructure().size(), 4);
		assertEquals(
				ModelUtil.getObjects(blackboard.getArchitectureModel().getSystem(), AssemblyConnector.class).size(), 3);
		assertEquals(ModelUtil
				.getObjects(blackboard.getArchitectureModel().getSystem(), ProvidedDelegationConnector.class).size(),
				1);

		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(0, countAssembly(blackboard.getArchitectureModel().getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));
		assertEquals(0, countAssembly(blackboard.getArchitectureModel().getSystem(), "_vFEekBXdEeqKY-U3QOe1UQ"));
		assertEquals(1, countAssembly(blackboard.getArchitectureModel().getSystem(), "_RMCOwBXcEeqKY-U3QOe1UQ"));

		AssemblyContext aLogic = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ")
				.get(0);
		AssemblyContext aDB2 = getAssemblys(blackboard.getArchitectureModel().getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ")
				.get(0);
		AssemblyContext aTranslator2 = getAssemblys(blackboard.getArchitectureModel().getSystem(),
				"_RMCOwBXcEeqKY-U3QOe1UQ").get(0);

		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(blackboard.getArchitectureModel().getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(1),
				aTranslator2, aTranslator2.getEncapsulatedComponent__AssemblyContext()
						.getProvidedRoles_InterfaceProvidingEntity().get(0)));

	}

}
