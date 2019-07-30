package dmodel.pipeline.records.instrument.spoon;

import java.io.File;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import dmodel.pipeline.records.instrument.ApplicationProject;
import dmodel.pipeline.records.instrument.InstrumentationMetadata;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.correspondence.CorrespondenceUtil;
import dmodel.pipeline.shared.pcm.PCMUtils;
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

// TODO please refactor this soon, otherwise i need to puke
public class SpoonApplicationTransformerTest {

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
				"_2nvWUKKQEem6I6QlOar_-g");
		ResourceDemandingSEFF seffEras = PCMUtils.getElementById(meta.getRepository(), ResourceDemandingSEFF.class,
				"_PlFlUJYHEempGaXtj6ezAw");
		ResourceDemandingSEFF seffGenerate = PCMUtils.getElementById(meta.getRepository(), ResourceDemandingSEFF.class,
				"_2RDcwKMhEemdKJpkeqfUZw");

		CtMethod<?> methDumb = model.getModel().filterChildren(new TypeFilter<CtMethod<?>>(CtMethod.class))
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
		CtMethod<?> methEras = model.getModel().filterChildren(new TypeFilter<CtMethod<?>>(CtMethod.class))
				.filterChildren(new Filter<CtMethod<?>>() {
					@Override
					public boolean matches(CtMethod<?> element) {
						if (element.getParent() instanceof CtClass) {
							CtClass<?> parent = (CtClass<?>) element.getParent();
							if (parent.getQualifiedName().contains("EratosthenesGeneratorImpl")) {
								return element.getSimpleName().equals("generatePrimes");
							}
						}
						return false;
					}
				}).first();

		CtMethod<?> methGenerate = model.getModel().filterChildren(new TypeFilter<CtMethod<?>>(CtMethod.class))
				.filterChildren(new Filter<CtMethod<?>>() {
					@Override
					public boolean matches(CtMethod<?> element) {
						if (element.getParent() instanceof CtClass) {
							CtClass<?> parent = (CtClass<?>) element.getParent();
							if (parent.getQualifiedName().contains("PrimeManagerImpl")) {
								return element.getSimpleName().equals("generatePrimes");
							}
						}
						return false;
					}
				}).first();

		corr.linkService(methDumb, seffDumb);
		corr.linkService(methEras, seffEras);
		corr.linkService(methGenerate, seffGenerate);

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
