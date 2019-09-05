package dmodel.pipeline.rt.rest.dt.container;

import org.palladiosimulator.pcm.system.System;
import org.springframework.stereotype.Service;

import dmodel.pipeline.shared.structure.DirectedGraph;

@Service
public class DesignTimeSystemDataContainer {

	private DirectedGraph<String, Integer> callGraph;
	private System currentSystem;

	public DirectedGraph<String, Integer> getCallGraph() {
		return callGraph;
	}

	public void setCallGraph(DirectedGraph<String, Integer> callGraph) {
		this.callGraph = callGraph;
	}

	public System getCurrentSystem() {
		return currentSystem;
	}

	public void setCurrentSystem(System currentSystem) {
		this.currentSystem = currentSystem;
	}

}
