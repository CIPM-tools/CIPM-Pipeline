package dmodel.pipeline.rt.rest.dt.async;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.system.ISystemCompositionAnalyzer;
import dmodel.pipeline.models.mapping.RepositoryMapping;
import dmodel.pipeline.records.instrument.ApplicationProject;
import dmodel.pipeline.records.instrument.IApplicationInstrumenter;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondence;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondenceUtil;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.border.RunTimeDesignTimeBorder;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.config.ProjectConfiguration;
import dmodel.pipeline.shared.util.AbstractObservable;
import lombok.AllArgsConstructor;
import spoon.Launcher;

@AllArgsConstructor
public class BuildServiceCallGraphProcess extends AbstractObservable<ServiceCallGraph> implements Runnable {

	private ProjectConfiguration config;
	private ISystemCompositionAnalyzer analyzer;
	private RunTimeDesignTimeBorder border;
	private IApplicationInstrumenter transformer;
	private RuntimePipelineBlackboard blackboard;

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
		ServiceCallGraph callGraph = analyzer.deriveSystemComposition(model, spoonMapping);
		border.setServiceCallGraph(callGraph);
		this.flood(callGraph);
	}

}
