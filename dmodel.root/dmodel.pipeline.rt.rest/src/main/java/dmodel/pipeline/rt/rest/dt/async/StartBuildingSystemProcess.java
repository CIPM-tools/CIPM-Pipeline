package dmodel.pipeline.rt.rest.dt.async;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.system.pcm.data.AbstractConflict;
import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder;
import dmodel.pipeline.shared.util.AbstractObservable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StartBuildingSystemProcess extends AbstractObservable<AbstractConflict<?>> implements Runnable {
	private PCMSystemBuilder builder;
	private ServiceCallGraph graph;

	@Override
	public void run() {
		boolean finished = builder.startBuildingSystem(graph);
		if (finished) {
			this.flood(null);
		} else {
			this.flood(builder.getCurrentConflict());
		}
	}

}
