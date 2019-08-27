package dmodel.pipeline.rt.rest.dt.async;

import dmodel.pipeline.dt.system.ISystemCompositionAnalyzer;
import dmodel.pipeline.models.mapping.RepositoryMapping;
import dmodel.pipeline.records.instrument.ApplicationProject;
import dmodel.pipeline.records.instrument.IApplicationInstrumenter;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondence;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondenceUtil;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.config.ProjectConfiguration;
import dmodel.pipeline.shared.structure.DirectedGraph;
import dmodel.pipeline.shared.util.AbstractObservable;
import spoon.Launcher;

public class BuildServiceCallGraphProcess extends AbstractObservable<DirectedGraph<String, Integer>>
		implements Runnable {

	private ProjectConfiguration config;
	private ISystemCompositionAnalyzer analyzer;
	private RuntimePipelineBlackboard blackboard;
	private IApplicationInstrumenter transformer;

	public BuildServiceCallGraphProcess(ProjectConfiguration config, RuntimePipelineBlackboard blackboard,
			ISystemCompositionAnalyzer analyzer, IApplicationInstrumenter transformer) {
		this.config = config;
		this.analyzer = analyzer;
		this.blackboard = blackboard;
		this.transformer = transformer;
	}

	@Override
	public void run() {
		// load the project
		ApplicationProject project = new ApplicationProject();
		project.setRootPath(config.getRootPath());
		project.setSourceFolders(config.getSourceFolders());

		// create model
		Launcher model = transformer.createModel(project);

		// load correspondence
		RepositoryMapping fileBackedMapping = ModelUtil.readFromFile(config.getCorrespondencePath(),
				RepositoryMapping.class);
		SpoonCorrespondence spoonMapping = SpoonCorrespondenceUtil.buildFromMapping(fileBackedMapping, model.getModel(),
				blackboard.getArchitectureModel().getRepository());

		// extract
		this.flood(analyzer.deriveSystemComposition(model, spoonMapping));
	}

}
