package cipm.consistency.app.rest.dt.data.scg;

import java.util.List;

import com.google.common.collect.Lists;

import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph;
import lombok.Data;

@Data
public class JsonServiceCallGraph {
	private List<JsonServiceCallGraphNode> nodes;
	private List<JsonServiceCallGraphEdge> edges;

	public JsonServiceCallGraph() {
		nodes = Lists.newArrayList();
		edges = Lists.newArrayList();
	}

	public static JsonServiceCallGraph from(ServiceCallGraph scg) {
		JsonServiceCallGraph result = new JsonServiceCallGraph();
		if (scg == null) {
			return result;
		}

		scg.getNodes().forEach(n -> {
			JsonServiceCallGraphNode nNode = new JsonServiceCallGraphNode();
			nNode.setContainerName(n.getHost() == null ? "" : n.getHost().getEntityName());
			nNode.setId(n.getSeff().getId());
			nNode.setName(n.getSeff().getDescribedService__SEFF().getEntityName());
			nNode.setParentId(n.getSeff().getBasicComponent_ServiceEffectSpecification().getId());
			nNode.setParentName(n.getSeff().getBasicComponent_ServiceEffectSpecification().getEntityName());

			result.getNodes().add(nNode);
		});

		scg.getEdges().forEach(e -> {
			JsonServiceCallGraphEdge nEdge = new JsonServiceCallGraphEdge();

			nEdge.setExtName(e.getExternalCall().getEntityName());
			nEdge.setFrom(e.getFrom().getSeff().getId());
			nEdge.setTo(e.getTo().getSeff().getId());

			result.getEdges().add(nEdge);
		});

		return result;
	}

}
