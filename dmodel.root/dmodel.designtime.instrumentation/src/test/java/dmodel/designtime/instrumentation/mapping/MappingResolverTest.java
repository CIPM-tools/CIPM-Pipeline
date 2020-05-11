package dmodel.designtime.instrumentation.mapping;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

import dmodel.base.core.AbstractCoreTest;
import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.base.vsum.domains.java.JavaCorrespondenceModelImpl;
import dmodel.designtime.instrumentation.project.ApplicationProject;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;
import dmodel.designtime.instrumentation.transformation.IApplicationProjectInstrumenter;
import dmodel.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterImpl;

public class MappingResolverTest {

	public static void main(String[] args) {

		ApplicationProject project = new ApplicationProject();
		project.setRootPath(AbstractCoreTest.REXAMPLE_PATH);
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
				fail("Failed to remove output folder.");
			}
		}
		outputFolder.mkdir();

		parsed.saveSources(outputFolder);

	}

}
