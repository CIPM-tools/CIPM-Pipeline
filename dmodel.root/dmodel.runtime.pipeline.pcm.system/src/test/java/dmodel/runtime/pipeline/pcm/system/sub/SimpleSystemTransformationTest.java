package dmodel.runtime.pipeline.pcm.system.sub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

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

import com.beust.jcommander.internal.Lists;

import dmodel.base.core.IPcmModelProvider;
import dmodel.base.shared.ModelUtil;
import dmodel.base.shared.structure.Tree;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.runtime.pipeline.pcm.system.AbstractBaseSystemTransformationTest;
import dmodel.runtime.pipeline.pcm.system.RuntimeSystemTransformation;

@RunWith(SpringRunner.class)
@Import(AbstractBaseSystemTransformationTest.SystemTransformationTestConfiguration.class)
public class SimpleSystemTransformationTest extends AbstractBaseSystemTransformationTest {

	@Autowired
	private IPcmModelProvider pcm;

	@Autowired
	private RuntimeSystemTransformation transformation;

	@Override
	protected void loadModels() {
		setSpecific(null, null, null);
		setPcm(ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test.repository"), Repository.class),
				ModelUtil.readFromResource(
						AbstractBaseSystemTransformationTest.class.getResource("/models/test.system"),
						org.palladiosimulator.pcm.system.System.class),
				ModelUtil.readFromResource(
						AbstractBaseSystemTransformationTest.class.getResource("/models/test.resourceenvironment"),
						ResourceEnvironment.class),
				ModelUtil.readFromResource(
						AbstractBaseSystemTransformationTest.class.getResource("/models/test.allocation"),
						Allocation.class),
				null);
	}

	@Test
	public void noMonitoringDataTest() {
		List<Tree<ServiceCallRecord>> records = Lists.newArrayList();

		transformation.transformSystem(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));
		assertTrue(modelsEqual(INIT_ALLOCATION, pcm.getAllocation()));
	}

	@Test
	public void noModificationTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/nomodification.dat");
		assertEquals(records.size(), 3);

		transformation.transformSystem(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));
		assertTrue(modelsEqual(INIT_ALLOCATION, pcm.getAllocation()));
	}

	@Test
	public void deprecationTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/dbchange1.dat");
		List<Tree<ServiceCallRecord>> records2 = Lists.newArrayList();

		transformation.transformSystem(records);
		transformation.transformSystem(records2);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));

		// system should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertEquals(pcm.getSystem().getConnectors__ComposedStructure().size(), 3);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), AssemblyConnector.class).size(), 2);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), ProvidedDelegationConnector.class).size(), 1);

		assertEquals(1, countAssembly(pcm.getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(0, countAssembly(pcm.getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));

		AssemblyContext aLogic = getAssemblys(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ").get(0);
		AssemblyContext aDB2 = getAssemblys(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ").get(0);

		assertTrue(isConnected(pcm.getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));

	}

	@Test
	public void dbChangeTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/dbchange1.dat");
		assertEquals(records.size(), 2);

		transformation.transformSystem(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));

		// system should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertEquals(pcm.getSystem().getConnectors__ComposedStructure().size(), 3);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), AssemblyConnector.class).size(), 2);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), ProvidedDelegationConnector.class).size(), 1);

		assertEquals(1, countAssembly(pcm.getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(0, countAssembly(pcm.getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));

		AssemblyContext aLogic = getAssemblys(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ").get(0);
		AssemblyContext aDB2 = getAssemblys(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ").get(0);

		assertTrue(isConnected(pcm.getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));

	}

	@Test
	public void changeBackTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/changeback1.dat");
		assertEquals(records.size(), 3);

		transformation.transformSystem(records);

		// check models after
		assertTrue(modelsEqual(INIT_REPOSITORY, pcm.getRepository()));
		assertTrue(modelsEqual(INIT_RESENV, pcm.getResourceEnvironment()));

		assertEquals(1, countAssembly(pcm.getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(0, countAssembly(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));

		AssemblyContext aLogic = getAssemblys(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ").get(0);
		AssemblyContext aDB1 = getAssemblys(pcm.getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ").get(0);

		assertTrue(isConnected(pcm.getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB1,
				aDB1.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
	}

	@Test
	public void stackedChangesTest() {
		List<Tree<ServiceCallRecord>> records = parseMonitoringResource("/monitoring/changeback1.dat");
		List<Tree<ServiceCallRecord>> records2 = parseMonitoringResource("/monitoring/dbchange1.dat");

		transformation.transformSystem(records);
		transformation.transformSystem(records2);

		// system should have changed
		assertFalse(modelsEqual(INIT_SYSTEM, pcm.getSystem()));
		assertEquals(pcm.getSystem().getConnectors__ComposedStructure().size(), 3);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), AssemblyConnector.class).size(), 2);
		assertEquals(ModelUtil.getObjects(pcm.getSystem(), ProvidedDelegationConnector.class).size(), 1);

		assertEquals(1, countAssembly(pcm.getSystem(), "_aZ5BYBHqEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ"));
		assertEquals(0, countAssembly(pcm.getSystem(), "_l7CT4BHrEeqXP_Rw8ZOxlQ"));
		assertEquals(1, countAssembly(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ"));

		AssemblyContext aLogic = getAssemblys(pcm.getSystem(), "_gcoKcBHrEeqXP_Rw8ZOxlQ").get(0);
		AssemblyContext aDB2 = getAssemblys(pcm.getSystem(), "_q0eIABHrEeqXP_Rw8ZOxlQ").get(0);

		assertTrue(isConnected(pcm.getSystem(), aLogic,
				aLogic.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().get(0),
				aDB2,
				aDB2.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().get(0)));
	}

}
