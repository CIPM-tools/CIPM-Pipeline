package dmodel.base.core;

import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraph;

/**
 * Interface which can be used to provide service call graphs (SCGs).
 * 
 * @author David Monschein
 *
 */
public interface ICallGraphProvider {

	/**
	 * Provides the current service call graph (SCG).
	 * 
	 * @return the current service call graph
	 */
	public ServiceCallGraph provideCallGraph();

	/**
	 * Determines whether there is a SCG currently being stored in this instance
	 * 
	 * @return true if there is an existing SCG, false if not
	 */
	public boolean callGraphPresent();

}
