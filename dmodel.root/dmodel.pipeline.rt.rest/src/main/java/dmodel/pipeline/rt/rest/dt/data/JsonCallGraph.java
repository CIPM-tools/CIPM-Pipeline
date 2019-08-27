package dmodel.pipeline.rt.rest.dt.data;

import java.util.ArrayList;
import java.util.List;

public class JsonCallGraph {

	private List<JsonCallGraphNode> nodes;
	private List<JsonCallGraphEdge> edges;

	public JsonCallGraph() {
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
	}

	public List<JsonCallGraphNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<JsonCallGraphNode> nodes) {
		this.nodes = nodes;
	}

	public List<JsonCallGraphEdge> getEdges() {
		return edges;
	}

	public void setEdges(List<JsonCallGraphEdge> edges) {
		this.edges = edges;
	}

}
