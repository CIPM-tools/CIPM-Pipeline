package dmodel.pipeline.records.instrument.spoon;

import java.io.File;

import org.junit.Test;
import org.palladiosimulator.pcm.repository.Repository;

import dmodel.pipeline.models.mapping.MappingPackage;
import dmodel.pipeline.models.mapping.RepositoryMapping;
import dmodel.pipeline.records.instrument.ApplicationProject;
import dmodel.pipeline.records.instrument.InstrumentationMetadata;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.correspondence.CorrespondenceUtil;
import dmodel.pipeline.shared.pcm.PCMUtils;
import spoon.Launcher;
import spoon.compiler.Environment;
import tools.vitruv.models.im.ImFactory;
import tools.vitruv.models.im.InstrumentationModel;
import tools.vitruv.models.im.InstrumentationPoint;
import tools.vitruv.models.im.InstrumentationType;

// TODO please refactor this soon, otherwise i need to puke
public class SpoonApplicationTransformerTest {

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
		Launcher model = transformer.prepareModifiableModel(project, configAgent,
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.rexample.instrumented");

		// load correspondence
		RepositoryMapping fileBackedMapping = ModelUtil.readFromFile("correspondence/spoon.corr",
				RepositoryMapping.class);
		SpoonCorrespondence spoonMapping = SpoonCorrespondenceUtil.buildFromMapping(fileBackedMapping, model.getModel(),
				meta.getRepository());

		// build instrumentation model
		InstrumentationModel iModel = ImFactory.eINSTANCE.createInstrumentationModel();
		InstrumentationPoint point = ImFactory.eINSTANCE.createInstrumentationPoint();
		point.setIsActive(true);
		point.setItype(InstrumentationType.SERVICE);
		point.setServiceID("_2nvWUKKQEem6I6QlOar_-g");
		iModel.getProbes().add(point);

		InstrumentationPoint point2 = ImFactory.eINSTANCE.createInstrumentationPoint();
		point2.setIsActive(true);
		point2.setItype(InstrumentationType.SERVICE);
		point2.setServiceID("_PlFlUJYHEempGaXtj6ezAw");
		iModel.getProbes().add(point2);

		InstrumentationPoint point3 = ImFactory.eINSTANCE.createInstrumentationPoint();
		point3.setIsActive(true);
		point3.setItype(InstrumentationType.SERVICE);
		point3.setServiceID("_2RDcwKMhEemdKJpkeqfUZw");
		iModel.getProbes().add(point3);

		meta.setProbes(iModel);

		// instrument it
		transformer.instrumentApplication(model, meta, spoonMapping);

		// set environment
		Environment environment = model.getEnvironment();
		environment.setCommentEnabled(true);
		environment.setAutoImports(true);

		// save back
		model.setSourceOutputDirectory(new File(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.rexample.instrumented/src/main/java"));
		model.prettyprint();
	}

}
