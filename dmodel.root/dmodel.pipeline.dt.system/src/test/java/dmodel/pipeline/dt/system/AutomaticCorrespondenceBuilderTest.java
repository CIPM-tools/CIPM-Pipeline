package dmodel.pipeline.dt.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.palladiosimulator.pcm.repository.Repository;

import dmodel.pipeline.dt.system.impl.StaticCodeReferenceAnalyzer;
import dmodel.pipeline.models.mapping.MappingPackage;
import dmodel.pipeline.records.instrument.ApplicationProject;
import dmodel.pipeline.records.instrument.spoon.SpoonApplicationTransformer;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondence;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.correspondence.CorrespondenceUtil;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
import spoon.Launcher;

public class AutomaticCorrespondenceBuilderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CorrespondenceUtil.initVitruv();
		PCMUtils.loadPCMModels();
		MappingPackage.eINSTANCE.eClass();
	}

	@Test
	public void test() {
		ApplicationProject project = new ApplicationProject();
		project.setRootPath(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.rexample/");
		project.getSourceFolders().add("src/main/java");

		// repository
		Repository repository = ModelUtil.readFromFile(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.rexample/models/prime_generator.repository",
				Repository.class);

		// parse
		SpoonApplicationTransformer transformer = new SpoonApplicationTransformer();
		Launcher model = transformer.createModel(project);

		// analyze it
		ISystemCompositionAnalyzer systemExtractor = new StaticCodeReferenceAnalyzer();

		// go for it
		SpoonCorrespondence corr = systemExtractor.resolveManualMapping(repository, model);

		assertTrue(corr != null);
		assertEquals(corr.getServiceMappingEntries().size(), 3);
	}

}
