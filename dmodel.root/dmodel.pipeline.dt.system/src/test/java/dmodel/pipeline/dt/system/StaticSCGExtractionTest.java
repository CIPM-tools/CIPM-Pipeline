package dmodel.pipeline.dt.system;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;

import dmodel.pipeline.core.AbstractCoreTest;
import dmodel.pipeline.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.system.impl.StaticCodeReferenceAnalyzer;
import dmodel.pipeline.instrumentation.mapping.AutomatedMappingResolverImpl;
import dmodel.pipeline.instrumentation.mapping.IAutomatedMappingResolver;
import dmodel.pipeline.instrumentation.project.ApplicationProject;
import dmodel.pipeline.instrumentation.project.ParsedApplicationProject;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.pipeline.vsum.domains.java.JavaCorrespondenceModelImpl;

@Import(AbstractCoreTest.CoreContextConfiguration.class)
@RunWith(SpringRunner.class)
public class StaticSCGExtractionTest extends AbstractCoreTest {
	private static final Repository PRIME_GENERATOR_REPOSITORY = ModelUtil
			.readFromFile("../dmodel.pipeline.rexample/models/prime_generator.repository", Repository.class);

	@Autowired
	private IRepositoryQueryFacade repositoryQuery;

	@Before
	public void setModels() {
		this.setPcm(PRIME_GENERATOR_REPOSITORY, null, null, null, null);
	}

	@Test
	public void extractionTest() {
		ApplicationProject project = new ApplicationProject();
		project.setRootPath("../dmodel.pipeline.rexample");
		project.setSourceFolders(Lists.newArrayList("src/main/java"));

		ParsedApplicationProject parsed = new ParsedApplicationProject(project);
		IAutomatedMappingResolver resolver = new AutomatedMappingResolverImpl();
		IJavaPCMCorrespondenceModel cpm = new JavaCorrespondenceModelImpl();

		resolver.resolveMappings(parsed, cpm);

		StaticCodeReferenceAnalyzer analyzer = new StaticCodeReferenceAnalyzer();
		File jarFile = new File("../dmodel.pipeline.rexample/build/libs/dmodel.pipeline.rexample.jar");
		ServiceCallGraph scg = analyzer.deriveSystemComposition(parsed, Lists.newArrayList(jarFile), repositoryQuery,
				cpm);

		// assertions
		// TODO
	}

}
