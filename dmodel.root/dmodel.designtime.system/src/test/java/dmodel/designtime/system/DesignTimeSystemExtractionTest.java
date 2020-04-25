package dmodel.designtime.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;

import dmodel.base.core.AbstractCoreTest;
import dmodel.base.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.base.shared.ModelUtil;
import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.base.vsum.domains.java.JavaCorrespondenceModelImpl;
import dmodel.designtime.instrumentation.mapping.AutomatedMappingResolverImpl;
import dmodel.designtime.instrumentation.mapping.IAutomatedMappingResolver;
import dmodel.designtime.instrumentation.project.ApplicationProject;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;
import dmodel.designtime.system.impl.StaticCodeReferenceAnalyzer;
import dmodel.designtime.system.pcm.data.ConnectionConflict;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder;
import dmodel.designtime.system.pcm.impl.util.ConflictBuilder;

@Import(AbstractCoreTest.CoreContextConfiguration.class)
@RunWith(SpringRunner.class)
public class DesignTimeSystemExtractionTest extends AbstractCoreTest {
	private static final Repository PRIME_GENERATOR_REPOSITORY = ModelUtil
			.readFromFile(REXAMPLE_PATH + "models/prime_generator.repository", Repository.class);
	private static final String PRIME_MANAGER_INTERFACE_ID = "_lx40IJYGEempGaXtj6ezAw";

	@TestConfiguration
	public static class DTExtractionContextConfiguration {

		@Bean
		public PCMSystemBuilder provideSystemBuilder() {
			return new PCMSystemBuilder();
		}

		@Bean
		public ConflictBuilder provideConflictBuilder() {
			return new ConflictBuilder();
		}

	}

	@Autowired
	private IRepositoryQueryFacade repositoryQuery;

	@Autowired
	private PCMSystemBuilder systemBuilder;

	@Before
	public void setModels() {
		this.setPcm(PRIME_GENERATOR_REPOSITORY, null, null, null, null);
	}

	@Test
	public void systemExtractionTest() {
		// get scg from here
		ApplicationProject project = new ApplicationProject();
		project.setRootPath(REXAMPLE_PATH);
		project.setSourceFolders(Lists.newArrayList("src/main/java"));

		ParsedApplicationProject parsed = new ParsedApplicationProject(project);
		IAutomatedMappingResolver resolver = new AutomatedMappingResolverImpl();
		IJavaPCMCorrespondenceModel cpm = new JavaCorrespondenceModelImpl();

		resolver.resolveMappings(parsed, cpm);

		StaticCodeReferenceAnalyzer analyzer = new StaticCodeReferenceAnalyzer();
		File jarFile = new File("test-data/libs/dmodel.example.primes.jar");
		ServiceCallGraph scg = analyzer.deriveSystemComposition(parsed, Lists.newArrayList(jarFile), repositoryQuery,
				cpm);

		// build system from here
		OperationInterface toProvide = repositoryQuery.getOperationInterface(PRIME_MANAGER_INTERFACE_ID);

		boolean state = systemBuilder.startBuildingSystem(scg, Lists.newArrayList(toProvide));
		assertFalse(state);
		assertEquals(ConnectionConflict.class, systemBuilder.getCurrentConflict().getClass());

		ConnectionConflict conflict = (ConnectionConflict) systemBuilder.getCurrentConflict();
		conflict.setSolution(conflict.getSolutions().get(0));
		conflict.setSolved(true);
		state = systemBuilder.continueBuilding();

		assertTrue(state);

	}

}
