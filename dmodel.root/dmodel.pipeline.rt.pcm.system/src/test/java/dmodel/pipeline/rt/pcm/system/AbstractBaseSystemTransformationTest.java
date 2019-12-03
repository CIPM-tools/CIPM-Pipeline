package dmodel.pipeline.rt.pcm.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;

import dmodel.pipeline.rt.pipeline.AbstractTransformationTest;
import dmodel.pipeline.shared.ModelUtil;

public abstract class AbstractBaseSystemTransformationTest extends AbstractTransformationTest {
	protected static Repository INIT_REPOSITORY;
	protected static System INIT_SYSTEM;
	protected static ResourceEnvironment INIT_RESENV;
	protected static Allocation INIT_ALLOCATION;

	protected static final String HOSTID_CLIENT = "client";
	protected static final String HOSTID_LOGIC = "logic";
	protected static final String HOSTID_DB = "db";
	protected static final String HOSTID_DB2 = "db2";

	@BeforeClass
	public static void loadInitModels() {
		INIT_REPOSITORY = ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test.repository"), Repository.class);

		INIT_SYSTEM = ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test.system"),
				org.palladiosimulator.pcm.system.System.class);

		INIT_RESENV = ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test.resourceenvironment"),
				ResourceEnvironment.class);

		INIT_ALLOCATION = ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/models/test.allocation"), Allocation.class);
	}

	@Before
	public void initHostMapping() {
		this.addHostMapping("_l5HWYBHtEeqXP_Rw8ZOxlQ", HOSTID_CLIENT);
		this.addHostMapping("_nyc2kRHtEeqXP_Rw8ZOxlQ", HOSTID_LOGIC);
		this.addHostMapping("_pvtNcRHtEeqXP_Rw8ZOxlQ", HOSTID_DB);
		this.addHostMapping("_DJuGYRXjEeqKY-U3QOe1UQ", HOSTID_DB2);
	}

	@Test
	public void checkModels() {
		assertNotNull(blackboard.getArchitectureModel().getRepository());
		assertNotNull(blackboard.getArchitectureModel().getSystem());
		assertNotNull(blackboard.getArchitectureModel().getResourceEnvironmentModel());
		assertNotNull(blackboard.getArchitectureModel().getAllocationModel());

		assertEquals(blackboard.getBorder().getRuntimeMapping().getHostMappings().size(), 4);
	}

	protected long countAssembly(System system, String comp) {
		return ModelUtil.getObjects(system, AssemblyContext.class).stream()
				.filter(f -> f.getEncapsulatedComponent__AssemblyContext().getId().equals(comp)).count();
	}

	protected List<AssemblyContext> getAssemblys(System system, String comp) {
		return ModelUtil.getObjects(system, AssemblyContext.class).stream()
				.filter(f -> f.getEncapsulatedComponent__AssemblyContext().getId().equals(comp))
				.collect(Collectors.toList());
	}

	protected boolean isConnected(System system, AssemblyContext req, RequiredRole rreq, AssemblyContext prov,
			ProvidedRole pprov) {
		return ModelUtil.getObjects(system, AssemblyConnector.class).stream().anyMatch(c -> {
			return c.getProvidedRole_AssemblyConnector().getId().equals(pprov.getId())
					&& c.getProvidingAssemblyContext_AssemblyConnector().getId().equals(prov.getId())
					&& c.getRequiredRole_AssemblyConnector().getId().equals(rreq.getId())
					&& c.getRequiringAssemblyContext_AssemblyConnector().getId().equals(req.getId());
		});
	}

}
