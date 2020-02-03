package dmodel.pipeline.records.instrument.spoon;

import java.io.File;

import org.junit.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.pcm.headless.api.util.PCMUtil;

import InstrumentationMetamodel.InstrumentationModel;
import InstrumentationMetamodel.InstrumentationModelFactory;
import InstrumentationMetamodel.ServiceInstrumentationPoint;
import dmodel.pipeline.models.mapping.MappingPackage;
import dmodel.pipeline.models.mapping.RepositoryMapping;
import dmodel.pipeline.records.instrument.ApplicationProject;
import dmodel.pipeline.records.instrument.InstrumentationMetadata;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.correspondence.CorrespondenceUtil;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
import spoon.Launcher;
import spoon.compiler.Environment;

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
		InstrumentationModel iModel = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();
		ServiceInstrumentationPoint point = InstrumentationModelFactory.eINSTANCE.createServiceInstrumentationPoint();
		point.setActive(true);
		point.setService(
				PCMUtil.getElementById(meta.getRepository(), ResourceDemandingSEFF.class, "_2nvWUKKQEem6I6QlOar_-g"));
		iModel.getPoints().add(point);

		ServiceInstrumentationPoint point2 = InstrumentationModelFactory.eINSTANCE.createServiceInstrumentationPoint();
		point2.setActive(true);
		point2.setService(
				PCMUtil.getElementById(meta.getRepository(), ResourceDemandingSEFF.class, "_PlFlUJYHEempGaXtj6ezAw"));
		iModel.getPoints().add(point2);

		ServiceInstrumentationPoint point3 = InstrumentationModelFactory.eINSTANCE.createServiceInstrumentationPoint();
		point3.setActive(true);
		point3.setService(
				PCMUtil.getElementById(meta.getRepository(), ResourceDemandingSEFF.class, "_2RDcwKMhEemdKJpkeqfUZw"));
		iModel.getPoints().add(point3);

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
