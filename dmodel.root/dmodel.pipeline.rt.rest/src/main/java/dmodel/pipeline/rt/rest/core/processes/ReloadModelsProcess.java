package dmodel.pipeline.rt.rest.core.processes;

import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.config.ModelConfiguration;

public class ReloadModelsProcess implements Runnable {

	private RuntimePipelineBlackboard blackboard;
	private ModelConfiguration config;

	public ReloadModelsProcess(RuntimePipelineBlackboard blackboard, ModelConfiguration config) {
		this.blackboard = blackboard;
		this.config = config;
	}

	@Override
	public void run() {
		this.blackboard.loadArchitectureModel(config);
	}

}
