package dmodel.pipeline.rt.rest.dt.async;

import dmodel.pipeline.dt.system.pcm.data.AbstractConflict;
import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.structure.DirectedGraph;
import dmodel.pipeline.shared.util.AbstractObservable;

public class StartBuildingSystemProcess extends AbstractObservable<AbstractConflict<?>> implements Runnable {
	private PCMSystemBuilder builder;
	private DirectedGraph<String, Integer> graph;
	private RuntimePipelineBlackboard blackboard;

	public StartBuildingSystemProcess(DirectedGraph<String, Integer> graph, RuntimePipelineBlackboard blackboard,
			PCMSystemBuilder builder) {
		this.builder = builder;
		this.graph = graph;
		this.blackboard = blackboard;
	}

	@Override
	public void run() {
		builder.setRepository(blackboard.getArchitectureModel().getRepository());

		boolean finished = builder.startBuildingSystem(graph);
		if (finished) {
			this.flood(null);
		} else {
			this.flood(builder.getCurrentConflict());
		}
	}

}
