package dmodel.pipeline.rt.pcm.system;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.pipeline.core.facade.IRuntimeEnvironmentQueryFacade;
import dmodel.pipeline.rt.pipeline.AbstractPipelineTestBase;
import dmodel.pipeline.rt.pipeline.BasePipelineTestConfiguration;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.ModelUtil;

@RunWith(SpringRunner.class)
public abstract class AbstractBaseSystemTransformationTest extends AbstractPipelineTestBase {
	protected static Repository INIT_REPOSITORY;
	protected static System INIT_SYSTEM;
	protected static ResourceEnvironment INIT_RESENV;
	protected static Allocation INIT_ALLOCATION;

	protected static final String HOSTID_CLIENT = "client";
	protected static final String HOSTID_LOGIC = "logic";
	protected static final String HOSTID_DB = "db";
	protected static final String HOSTID_DB2 = "db2";
	protected static final String HOSTID_CLIENTNEW = "clientnew";
	protected static final String HOSTID_LOGICNEW = "logicnew";
	protected static final String HOSTID_DBNEW = "dbnew";

	@TestConfiguration
	public static class SystemTransformationTestConfiguration
			extends BasePipelineTestConfiguration.TestContextConfiguration {

		@Bean
		public RuntimeSystemTransformation transformation() {
			return new RuntimeSystemTransformation();
		}

		@Bean
		public RuntimeSystemUpdater updater() {
			return new RuntimeSystemUpdater();
		}

	}

	@Autowired
	private RuntimeSystemTransformation transformation;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private IRuntimeEnvironmentQueryFacade remQuery;

	@Before
	public void initTransformation() {
		transformation.setBlackboard(blackboard);
	}

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
		loadModels();
		super.reloadVsum();

		remQuery.createResourceContainer(HOSTID_CLIENT, HOSTID_CLIENT);
		remQuery.createResourceContainer(HOSTID_LOGIC, HOSTID_LOGIC);
		remQuery.createResourceContainer(HOSTID_DB, HOSTID_DB);
		remQuery.createResourceContainer(HOSTID_DB2, HOSTID_DB2);

		remQuery.createResourceContainer(HOSTID_CLIENTNEW, HOSTID_CLIENTNEW);
		remQuery.createResourceContainer(HOSTID_LOGICNEW, HOSTID_LOGICNEW);
		remQuery.createResourceContainer(HOSTID_DBNEW, HOSTID_DBNEW);
	}

	protected abstract void loadModels();

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
