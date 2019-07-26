package dmodel.pipeline.dt.system.pcm.graph;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ComposedStructure;

import dmodel.pipeline.shared.pcm.PCMUtils;

public class ComposedStructureConverter {

	public Graph<AssemblyNode, String> converToGraph(ComposedStructure struct) {
		Graph<AssemblyNode, String> nGraph = new DefaultDirectedGraph<AssemblyNode, String>(String.class);
		Map<AssemblyNode, AssemblyContext> ctxMapping = new HashMap<>();
		Map<AssemblyContext, AssemblyNode> ctxMappingRev = new HashMap<>();

		// nodes
		for (AssemblyContext ctx : PCMUtils.getElementsByType(struct, AssemblyContext.class)) {
			AssemblyNode nNode = new AssemblyNode();
			nNode.assemblyId = ctx.getId();
			nNode.componentId = ctx.getEncapsulatedComponent__AssemblyContext().getId();

			nGraph.addVertex(nNode);
			ctxMapping.put(nNode, ctx);
			ctxMappingRev.put(ctx, nNode);
		}

		// now connections
		for (AssemblyConnector aconn : PCMUtils.getElementsByType(struct, AssemblyConnector.class)) {
			String edgeId = aconn.getProvidedRole_AssemblyConnector().getId() + "-"
					+ aconn.getRequiredRole_AssemblyConnector().getId();
			AssemblyNode fromNode = ctxMappingRev.get(aconn.getRequiringAssemblyContext_AssemblyConnector());
			AssemblyNode toNode = ctxMappingRev.get(aconn.getProvidingAssemblyContext_AssemblyConnector());

			nGraph.addEdge(fromNode, toNode, edgeId);
		}

		return nGraph;
	}

	public class AssemblyNode {
		private String componentId;
		private String assemblyId;

		public String getComponentId() {
			return componentId;
		}

		public void setComponentId(String componentId) {
			this.componentId = componentId;
		}

		public String getAssemblyId() {
			return assemblyId;
		}

		public void setAssemblyId(String assemblyId) {
			this.assemblyId = assemblyId;
		}
	}

}
