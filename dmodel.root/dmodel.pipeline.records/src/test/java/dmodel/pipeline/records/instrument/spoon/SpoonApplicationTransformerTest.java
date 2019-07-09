package dmodel.pipeline.records.instrument.spoon;

import java.io.File;

import org.junit.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import dmodel.pipeline.records.instrument.ApplicationProject;
import dmodel.pipeline.records.instrument.InstrumentationMetadata;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.PCMUtils;
import dmodel.pipeline.shared.correspondence.CorrespondenceUtil;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import tools.vitruv.framework.correspondence.Correspondences;
import tools.vitruv.models.im.ImFactory;
import tools.vitruv.models.im.InstrumentationModel;
import tools.vitruv.models.im.InstrumentationPoint;
import tools.vitruv.models.im.InstrumentationType;

public class SpoonApplicationTransformerTest {

	@Test
	public void test() {
		CorrespondenceUtil.initVitruv();
		PCMUtils.loadPCMModels();

		Correspondences tempCorrs = CorrespondenceUtil
				.loadCorrespondenceModel(new File("correspondence/Correspondences.correspondence"));

		ApplicationProject project = new ApplicationProject();
		project.setRootPath(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.rexample/");
		project.getSourceFolders().add("src/main/java");

		SpoonApplicationTransformer transformer = new SpoonApplicationTransformer();

		InstrumentationMetadata meta = new InstrumentationMetadata();
		meta.setCorrespondence(tempCorrs);
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

		// build the correspondence manually
		SpoonCorrespondence corr = new SpoonCorrespondence(model.getModel(), meta.getRepository());
		ResourceDemandingSEFF seffDumb = PCMUtils.getElementById(meta.getRepository(), ResourceDemandingSEFF.class,
				"_PlFlUJYHEempGaXtj6ezAw");
		CtMethod<?> meth = model.getModel().filterChildren(new TypeFilter<CtMethod<?>>(CtMethod.class))
				.filterChildren(new Filter<CtMethod<?>>() {
					@Override
					public boolean matches(CtMethod<?> element) {
						if (element.getParent() instanceof CtClass) {
							CtClass<?> parent = (CtClass<?>) element.getParent();
							if (parent.getQualifiedName().contains("DumbGeneratorImpl")) {
								return element.getSimpleName().equals("generatePrimes");
							}
						}
						return false;
					}
				}).first();
		corr.linkService(meth, seffDumb);

		// build instrumentation model
		InstrumentationModel iModel = ImFactory.eINSTANCE.createInstrumentationModel();
		InstrumentationPoint point = ImFactory.eINSTANCE.createInstrumentationPoint();
		point.setIsActive(true);
		point.setItype(InstrumentationType.SERVICE);
		point.setServiceID("_PlFlUJYHEempGaXtj6ezAw");
		iModel.getProbes().add(point);

		meta.setProbes(iModel);

		// instrument it
		transformer.instrumentApplication(model, meta, corr);

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
