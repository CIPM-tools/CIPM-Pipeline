package dmodel.pipeline.instrumentation.transform;

import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.Lists;

import dmodel.pipeline.instrumentation.mapping.AutomatedMappingResolverImpl;
import dmodel.pipeline.instrumentation.mapping.IAutomatedMappingResolver;
import dmodel.pipeline.instrumentation.project.ApplicationProject;
import dmodel.pipeline.instrumentation.project.ParsedApplicationProject;
import dmodel.pipeline.instrumentation.project.app.ApplicationProjectTransformer;
import dmodel.pipeline.instrumentation.project.app.InstrumentationMetadata;
import dmodel.pipeline.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.pipeline.vsum.domains.java.JavaCorrespondenceModelImpl;

public class ApplicationTransformerTest {

	@Test
	public void transformPrimeExample() throws IOException {
		ApplicationProject project = new ApplicationProject();
		project.setRootPath("../dmodel.pipeline.rexample");
		project.setSourceFolders(Lists.newArrayList("src/main/java"));

		ParsedApplicationProject parsed = new ParsedApplicationProject(project);
		IAutomatedMappingResolver resolver = new AutomatedMappingResolverImpl();
		IJavaPCMCorrespondenceModel cpm = new JavaCorrespondenceModelImpl();

		resolver.resolveMappings(parsed, cpm);

		InstrumentationMetadata metadata = new InstrumentationMetadata();
		metadata.setOutputPath("../dmodel.pipeline.rexample.instrumented/");

		metadata.setHostName("localhost");
		metadata.setInmRestPath("/runtime/pipeline/imm");
		metadata.setRestPort(8090);
		metadata.setLogarithmicScaling(true);
		metadata.setLogarithmicRecoveryInterval(1000);

		ApplicationProjectTransformer transformer = new ApplicationProjectTransformer();
		transformer.performInstrumentation(project, metadata, cpm);
	}

}
