package dmodel.pipeline.dt.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.palladiosimulator.pcm.repository.Repository;

import InstrumentationMetamodel.InstrumentationModel;
import InstrumentationMetamodel.InstrumentationModelFactory;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.system.impl.StaticCodeReferenceAnalyzer;
import dmodel.pipeline.dt.system.pcm.data.ConnectionConflict;
import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder;
import dmodel.pipeline.models.mapping.MappingPackage;
import dmodel.pipeline.models.mapping.RepositoryMapping;
import dmodel.pipeline.records.instrument.ApplicationProject;
import dmodel.pipeline.records.instrument.InstrumentationMetadata;
import dmodel.pipeline.records.instrument.spoon.SpoonApplicationTransformer;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondence;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondenceUtil;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.correspondence.CorrespondenceUtil;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
import spoon.Launcher;

// TODO please refactor this soon, otherwise i need to puke
public class SystemDerivationTest {

	@Test
	public void test() {
		CorrespondenceUtil.initVitruv();
		PCMUtils.loadPCMModels();
		MappingPackage.eINSTANCE.eClass();

		ApplicationProject project = new ApplicationProject();
		project.setRootPath(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.rexample/");
		project.getSourceFolders().add("src/main/java");

		SpoonApplicationTransformer transformer = new SpoonApplicationTransformer();

		InstrumentationMetadata meta = new InstrumentationMetadata();
		meta.setRepository(ModelUtil.readFromFile(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.rexample/models/prime_generator.repository",
				Repository.class));

		// agent config
		ApplicationProject configAgent = new ApplicationProject();
		configAgent.setRootPath(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.monitoring/");
		configAgent.getSourceFolders().add("src/main/java");
		configAgent.getSourceFolders().add("src-gen/java");

		// copy and parse
		Launcher model = transformer.createModel(project);

		// load correspondence
		RepositoryMapping fileBackedMapping = ModelUtil.readFromFile("correspondence/spoon.corr",
				RepositoryMapping.class);
		SpoonCorrespondence spoonMapping = SpoonCorrespondenceUtil.buildFromMapping(fileBackedMapping, model.getModel(),
				meta.getRepository());

		// build instrumentation model
		InstrumentationModel iModel = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();

		// analyze it
		ISystemCompositionAnalyzer systemExtractor = new StaticCodeReferenceAnalyzer();
		ServiceCallGraph callGraph = systemExtractor.deriveSystemComposition(model, spoonMapping);

		// derive system
		PCMSystemBuilder extractor = new PCMSystemBuilder();
		boolean finished = extractor.startBuildingSystem(callGraph);
		assertFalse(finished);
		assertEquals(extractor.getCurrentConflict().getClass(), ConnectionConflict.class);

		ConnectionConflict conf = (ConnectionConflict) extractor.getCurrentConflict();
		conf.setSolved(true);
		conf.setSolution(conf.getProvided().get(0));

		finished = extractor.continueBuilding();
		assertTrue(finished);

		ModelUtil.saveToFile(extractor.getCurrentSystem(), "output/test1.system");
	}

}
