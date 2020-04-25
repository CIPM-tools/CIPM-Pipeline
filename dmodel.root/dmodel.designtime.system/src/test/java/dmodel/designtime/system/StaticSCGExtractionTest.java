package dmodel.designtime.system;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
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

@Import(AbstractCoreTest.CoreContextConfiguration.class)
@RunWith(SpringRunner.class)
public class StaticSCGExtractionTest extends AbstractCoreTest {
	private static final Repository PRIME_GENERATOR_REPOSITORY = ModelUtil
			.readFromFile(REXAMPLE_PATH + "models/prime_generator.repository", Repository.class);

	@Autowired
	private IRepositoryQueryFacade repositoryQuery;

	@Before
	public void setModels() {
		this.setPcm(PRIME_GENERATOR_REPOSITORY, null, null, null, null);
	}

	@Test
	public void extractionTest() {
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

		// assertions
		assertEquals(3, scg.getNodes().size());
		assertEquals(2, scg.getEdges().size());
	}

}
