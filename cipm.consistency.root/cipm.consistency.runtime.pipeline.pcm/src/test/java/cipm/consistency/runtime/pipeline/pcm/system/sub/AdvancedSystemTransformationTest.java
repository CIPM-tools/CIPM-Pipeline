package cipm.consistency.runtime.pipeline.pcm.system.sub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.runtime.pipeline.pcm.system.AbstractBaseSystemTransformationTest;
import cipm.consistency.runtime.pipeline.pcm.system.RuntimeSystemTransformation;

@RunWith(SpringRunner.class)
@Import(AbstractBaseSystemTransformationTest.SystemTransformationTestConfiguration.class)
public class AdvancedSystemTransformationTest extends AbstractBaseSystemTransformationTest {

	@Autowired
	private IPcmModelProvider pcm;

	@Autowired
	private RuntimeSystemTransformation transformation;

	@BeforeClass
	public static void overwriteDefaultModels() {
		INIT_ALLOCATION = ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test2.allocation"), Allocation.class);
	}

	@Override
	protected void loadModels() {
		setSpecific(null, null, null);
		setPcm(ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test.repository"), Repository.class),
				ModelUtil.readFromResource(
						AbstractBaseSystemTransformationTest.class.getResource("/models/test2.system"),
						org.palladiosimulator.pcm.system.System.class),
				ModelUtil.readFromResource(
						AbstractBaseSystemTransformationTest.class.getResource("/models/test.resourceenvironment"),
						ResourceEnvironment.class),
				ModelUtil.readFromResource(
						AbstractBaseSystemTransformationTest.class.getResource("/models/test2.allocation"),
						Allocation.class),
				null);
	}

	@Test
	public void fullEnvironmentChange() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/environmentchange.dat");
		assertEquals(records.size(), 2);

		transformation.transformSystem(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));

		// system & allocation should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertFalse(modelsEqual(INIT_ALLOCATION, pcm.getAllocation()));
	}

	@Test
	public void uiChangeTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/uichange.dat");
		assertEquals(records.size(), 3);

		transformation.transformSystem(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));

		// system should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertEquals(pcm.getSystem().getConnectors__ComposedStructure().size(), 5);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), AssemblyConnector.class).size(), 4);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), ProvidedDelegationConnector.class).size(), 1);

		assertEquals(5, pcm.getSystem().getAssemblyContexts__ComposedStructure().size());
		assertEquals(0, countAssembly(pcm.getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(0, countAssembly(pcm.getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(2, countAssembly(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_vFEekBXdEeqKY-U3QOe1UQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_lc4ZgBYjEeqKY-U3QOe1UQ"));

		AssemblyContext aLogic = getAssemblys(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ").get(0);
		AssemblyContext aUI2 = getAssemblys(pcm.getSystem(), "_lc4ZgBYjEeqKY-U3QOe1UQ").get(0);
		AssemblyContext aDB2 = getAssemblys(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ").get(1);
		AssemblyContext aTranslator1 = getAssemblys(pcm.getSystem(), "_vFEekBXdEeqKY-U3QOe1UQ").get(0);
		AssemblyContext aDB22 = getAssemblys(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ").get(0);

		ProvidedDelegationConnector prov = ModelUtil.getObjects(pcm.getSystem(), ProvidedDelegationConnector.class)
				.get(0);
		assertTrue(prov.getAssemblyContext_ProvidedDelegationConnector().getId().equals(aUI2.getId()));
		assertTrue(prov.getInnerProvidedRole_ProvidedDelegationConnector().getId()
				.equals(aUI2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity()
						.get(0).getId()));

		assertTrue(isConnected(pcm.getSystem(), aUI2,
				aUI2.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));

		assertTrue(isConnected(pcm.getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB22,
				aDB22.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(pcm.getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(1),
				aTranslator1, aTranslator1.getEncapsulatedComponent__AssemblyContext()
						.getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(pcm.getSystem(), aTranslator1,
				aTranslator1.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity()
						.get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
	}

	@Test
	public void dbChangeTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/dbchange2.dat");
		assertEquals(records.size(), 2);

		transformation.transformSystem(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));

		// system should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertEquals(pcm.getSystem().getConnectors__ComposedStructure().size(), 5);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), AssemblyConnector.class).size(), 4);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), ProvidedDelegationConnector.class).size(), 1);

		assertEquals(1, countAssembly(pcm.getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(0, countAssembly(pcm.getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(2, countAssembly(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_vFEekBXdEeqKY-U3QOe1UQ"));

		AssemblyContext aLogic = getAssemblys(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ").get(0);
		AssemblyContext aDB2 = getAssemblys(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ").get(1);
		AssemblyContext aTranslator1 = getAssemblys(pcm.getSystem(), "_vFEekBXdEeqKY-U3QOe1UQ").get(0);
		AssemblyContext aDB22 = getAssemblys(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ").get(0);

		assertTrue(isConnected(pcm.getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB22,
				aDB22.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(pcm.getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(1),
				aTranslator1, aTranslator1.getEncapsulatedComponent__AssemblyContext()
						.getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(pcm.getSystem(), aTranslator1,
				aTranslator1.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity()
						.get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));

	}

	@Test
	public void translatorChangeTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/translatorchange.dat");
		assertEquals(records.size(), 2);

		transformation.transformSystem(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));

		// ModelUtil.saveToFile(pcm.getSystem(),
		// "result.system");

		// system should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertEquals(pcm.getSystem().getConnectors__ComposedStructure().size(), 4);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), AssemblyConnector.class).size(), 3);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), ProvidedDelegationConnector.class).size(), 1);

		assertEquals(1, countAssembly(pcm.getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(0, countAssembly(pcm.getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));
		assertEquals(0, countAssembly(pcm.getSystem(), "_vFEekBXdEeqKY-U3QOe1UQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_RMCOwBXcEeqKY-U3QOe1UQ"));

		AssemblyContext aLogic = getAssemblys(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ").get(0);
		AssemblyContext aDB2 = getAssemblys(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ").get(0);
		AssemblyContext aTranslator2 = getAssemblys(pcm.getSystem(), "_RMCOwBXcEeqKY-U3QOe1UQ").get(0);

		assertTrue(isConnected(pcm.getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
		assertTrue(isConnected(pcm.getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(1),
				aTranslator2, aTranslator2.getEncapsulatedComponent__AssemblyContext()
						.getProvidedRoles_InterfaceProvidingEntity().get(0)));

	}

}
