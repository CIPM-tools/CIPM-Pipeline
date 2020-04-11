package dmodel.pipeline.rt.rest.dt.async;

import java.io.File;

import InstrumentationMetamodel.InstrumentationModel;
import InstrumentationMetamodel.InstrumentationModelFactory;
import InstrumentationMetamodel.ServiceInstrumentationPoint;
import dmodel.pipeline.core.config.ProjectConfiguration;
import dmodel.pipeline.models.mapping.RepositoryMapping;
import dmodel.pipeline.records.instrument.ApplicationProject;
import dmodel.pipeline.records.instrument.IApplicationInstrumenter;
import dmodel.pipeline.records.instrument.InstrumentationMetadata;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondence;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondenceUtil;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.rest.dt.data.InstrumentationStatus;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.util.AbstractObservable;
import spoon.Launcher;
import spoon.compiler.Environment;

public class InstrumentationProcess extends AbstractObservable<InstrumentationStatus> implements Runnable {

	private ProjectConfiguration config;
	private IApplicationInstrumenter transformer;
	private RuntimePipelineBlackboard blackboard;

	public InstrumentationProcess(ProjectConfiguration config, RuntimePipelineBlackboard blackboard,
			IApplicationInstrumenter transformer) {
		this.config = config;
		this.transformer = transformer;
		this.blackboard = blackboard;
	}

	@Override
	public void run() {
		this.flood(InstrumentationStatus.STARTED);
		// metadata
		InstrumentationMetadata meta = new InstrumentationMetadata();
		meta.setRepository(blackboard.getArchitectureModel().getRepository());

		// load the project
		ApplicationProject project = new ApplicationProject();
		project.setRootPath(config.getRootPath());
		project.setSourceFolders(config.getSourceFolders());

		// load the agent project
		// TODO hardcoded path..
		ApplicationProject configAgent = new ApplicationProject();
		configAgent.setRootPath(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.monitoring/");
		configAgent.getSourceFolders().add("src/main/java");
		configAgent.getSourceFolders().add("src-gen/java");

		// copy and parse
		Launcher model = transformer.prepareModifiableModel(project, configAgent, config.getInstrumentedPath());

		this.flood(InstrumentationStatus.PREPARED);

		// load correspondence
		RepositoryMapping fileBackedMapping = ModelUtil.readFromFile(config.getCorrespondencePath(),
				RepositoryMapping.class);
		SpoonCorrespondence spoonMapping = SpoonCorrespondenceUtil.buildFromMapping(fileBackedMapping, model.getModel(),
				meta.getRepository());

		// create all instrumentation points (for every service)
		InstrumentationModel iModel = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();
		spoonMapping.getServiceMappingEntries().forEach(entry -> {
			ServiceInstrumentationPoint point = InstrumentationModelFactory.eINSTANCE
					.createServiceInstrumentationPoint();
			point.setActive(true);
			point.setService(entry.getValue());
			iModel.getPoints().add(point);
		});
		meta.setProbes(iModel);

		// set environment
		Environment environment = model.getEnvironment();
		environment.setCommentEnabled(true);
		environment.setAutoImports(true);

		this.flood(InstrumentationStatus.INSTRUMENTATION);

		// instrument it
		transformer.instrumentApplication(model, meta, spoonMapping);

		this.flood(InstrumentationStatus.SAVING);

		// save back
		// TODO multiple projects and source folders?
		File sourceOutputFolder = new File(new File(config.getInstrumentedPath()), config.getSourceFolders().get(0));
		model.setSourceOutputDirectory(sourceOutputFolder);
		model.prettyprint();

		this.flood(InstrumentationStatus.FINISHED);
	}

}
