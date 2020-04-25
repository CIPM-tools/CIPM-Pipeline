package dmodel.designtime.instrumentation.transform;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;

import dmodel.base.core.AbstractCoreTest;
import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.base.vsum.domains.java.JavaCorrespondenceModelImpl;
import dmodel.designtime.instrumentation.mapping.AutomatedMappingResolverImpl;
import dmodel.designtime.instrumentation.mapping.IAutomatedMappingResolver;
import dmodel.designtime.instrumentation.project.ApplicationProject;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;
import dmodel.designtime.instrumentation.project.app.ApplicationProjectTransformer;
import dmodel.designtime.instrumentation.project.app.InstrumentationMetadata;

@RunWith(SpringRunner.class)
@Import(AbstractCoreTest.CoreContextConfiguration.class)
public class ApplicationTransformerTest extends AbstractCoreTest {

	@Test
	public void transformPrimeExample() throws IOException {
		ApplicationProject project = new ApplicationProject();
		project.setRootPath(REXAMPLE_PATH);
		project.setSourceFolders(Lists.newArrayList("src/main/java"));

		ParsedApplicationProject parsed = new ParsedApplicationProject(project);
		IAutomatedMappingResolver resolver = new AutomatedMappingResolverImpl();
		IJavaPCMCorrespondenceModel cpm = new JavaCorrespondenceModelImpl();

		resolver.resolveMappings(parsed, cpm);

		InstrumentationMetadata metadata = new InstrumentationMetadata();
		metadata.setOutputPath("../dmodel.example.primes.instrumented/");

		metadata.setHostName("localhost");
		metadata.setInmRestPath("/runtime/pipeline/imm");
		metadata.setRestPort(8090);
		metadata.setLogarithmicScaling(true);
		metadata.setLogarithmicRecoveryInterval(1000);

		ApplicationProjectTransformer transformer = new ApplicationProjectTransformer();
		transformer.performInstrumentation(project, metadata, cpm);
	}

}
