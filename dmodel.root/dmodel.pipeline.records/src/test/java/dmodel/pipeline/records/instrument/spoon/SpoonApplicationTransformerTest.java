package dmodel.pipeline.records.instrument.spoon;

import org.junit.Test;

import dmodel.pipeline.records.instrument.ApplicationProject;

public class SpoonApplicationTransformerTest {

	@Test
	public void test() {
		ApplicationProject project = new ApplicationProject();
		project.setRootPath(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.rexample/");
		project.getSourceFolders().add("src/main/java");

		SpoonApplicationTransformer transformer = new SpoonApplicationTransformer();
		transformer.instrumentApplication(project, null);
	}

}
