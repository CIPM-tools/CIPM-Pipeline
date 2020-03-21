package dmodel.pipeline.dt.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.system.impl.StaticCodeReferenceAnalyzer;
import dmodel.pipeline.dt.system.pcm.data.ConnectionConflict;
import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder;
import dmodel.pipeline.dt.system.pcm.impl.util.ConflictBuilder;
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

// TODO refactor absolute paths
@RunWith(SpringRunner.class)
public class SystemDerivationTest {

	@TestConfiguration
	static class SystemBuilderTestConfiguration {
		@Bean
		public PCMSystemBuilder systemBuilder() {
			return new PCMSystemBuilder();
		}

		@Bean
		public ConflictBuilder conflictBuilder() {
			return new ConflictBuilder();
		}
	}

	@Autowired
	private PCMSystemBuilder systemBuilder;

	@BeforeClass
	public static void setup() {
		CorrespondenceUtil.initVitruv();
		PCMUtils.loadPCMModels();
		MappingPackage.eINSTANCE.eClass();
	}

	@Test
	public void buildingTest() {

		ApplicationProject project = new ApplicationProject();
		project.setRootPath(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.rexample/");
		project.getSourceFolders().add("src/main/java");

		SpoonApplicationTransformer transformer = new SpoonApplicationTransformer();

		InstrumentationMetadata meta = new InstrumentationMetadata();
		meta.setRepository(ModelUtil.readFromFile(new File(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.rexample/models/prime_generator.repository")
						.getAbsolutePath(),
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

		// analyze it
		ISystemCompositionAnalyzer systemExtractor = new StaticCodeReferenceAnalyzer();
		ServiceCallGraph callGraph = systemExtractor.deriveSystemComposition(model, spoonMapping);
		callGraph.setRepository(meta.getRepository());

		// derive system
		OperationInterface systemProvidedRole = PCMUtils.getElementById(meta.getRepository(), OperationInterface.class,
				"_lx40IJYGEempGaXtj6ezAw");
		boolean finished = systemBuilder.startBuildingSystem(callGraph, Lists.newArrayList(systemProvidedRole));
		assertFalse(finished);
		assertEquals(systemBuilder.getCurrentConflict().getClass(), ConnectionConflict.class);

		ConnectionConflict conf = (ConnectionConflict) systemBuilder.getCurrentConflict();
		conf.setSolved(true);
		conf.setSolution(conf.getSolutions().get(0));

		finished = systemBuilder.continueBuilding();
		assertTrue(finished);

		ModelUtil.saveToFile(systemBuilder.getCurrentSystem(), "output/test1.system");
	}

}
