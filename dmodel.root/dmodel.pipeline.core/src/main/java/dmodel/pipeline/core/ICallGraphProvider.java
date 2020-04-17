package dmodel.pipeline.core;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;

public interface ICallGraphProvider {

	public ServiceCallGraph provideCallGraph();

	public boolean callGraphPresent();

}
