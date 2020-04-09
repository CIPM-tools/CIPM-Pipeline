package dmodel.pipeline.instrumentation.mapping;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

import dmodel.pipeline.instrumentation.project.ApplicationProject;
import dmodel.pipeline.instrumentation.project.ParsedApplicationProject;
import dmodel.pipeline.instrumentation.transformation.IApplicationProjectInstrumenter;
import dmodel.pipeline.instrumentation.transformation.impl.ApplicationProjectInstrumenterImpl;
import dmodel.pipeline.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.pipeline.vsum.domains.java.JavaCorrespondenceModelImpl;

public class MappingResolverTest {

	public static void main(String[] args) {

		ApplicationProject project = new ApplicationProject();
		project.setRootPath("../dmodel.pipeline.rexample");
		project.setSourceFolders(Lists.newArrayList("src/main/java"));

		ParsedApplicationProject parsed = new ParsedApplicationProject(project);

		IAutomatedMappingResolver resolver = new AutomatedMappingResolverImpl();
		IJavaPCMCorrespondenceModel cpm = new JavaCorrespondenceModelImpl();

		resolver.resolveMappings(parsed, cpm);

		IApplicationProjectInstrumenter projectInstrumenter = new ApplicationProjectInstrumenterImpl();
		projectInstrumenter.transform(parsed, cpm);

		File outputFolder = new File("output");
		if (outputFolder.exists()) {
			try {
				FileUtils.deleteDirectory(outputFolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		outputFolder.mkdir();

		parsed.saveSources(outputFolder);

	}

}
