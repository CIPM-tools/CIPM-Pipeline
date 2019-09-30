package dmodel.pipeline.rt.rest.dt;

import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.dt.system.impl.StaticCodeReferenceAnalyzer;
import dmodel.pipeline.records.instrument.IApplicationInstrumenter;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.rest.dt.async.BuildServiceCallGraphProcess;
import dmodel.pipeline.rt.rest.dt.container.DesignTimeSystemDataContainer;
import dmodel.pipeline.rt.rest.dt.data.JsonCallGraph;
import dmodel.pipeline.rt.rest.dt.data.JsonCallGraphEdge;
import dmodel.pipeline.rt.rest.dt.data.JsonCallGraphNode;
import dmodel.pipeline.shared.JsonUtil;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import dmodel.pipeline.shared.pcm.PCMUtils;
import dmodel.pipeline.shared.structure.DirectedGraph;

@RestController
public class SystemCallGraphRestController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private StaticCodeReferenceAnalyzer systemAnalyzer;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private DModelConfigurationContainer config;

	@Autowired
	private IApplicationInstrumenter transformer;

	@Autowired
	private ScheduledExecutorService executorService;

	@Autowired
	private DesignTimeSystemDataContainer dataContainer;

	// DATA
	private boolean callGraphBuilded = false;

	@GetMapping("/design/system/graph/finished")
	public String finishedGraph() {
		return JsonUtil.wrapAsObject("finished", callGraphBuilded, false);
	}

	@GetMapping("/design/system/graph/get")
	public String getGraph() {
		if (callGraphBuilded) {
			try {
				return objectMapper.writeValueAsString(convertCallGraphToJson(dataContainer.getCallGraph()));
			} catch (JsonProcessingException e) {
				return JsonUtil.emptyObject();
			}
		} else {
			return JsonUtil.emptyObject();
		}
	}

	@PostMapping("/design/system/graph")
	public String buildGraph() {
		if (config.getProject().getCorrespondencePath().equals("")) {
			return JsonUtil.emptyObject();
		}

		callGraphBuilded = false;

		// create processes
		BuildServiceCallGraphProcess process = new BuildServiceCallGraphProcess(config.getProject(), blackboard,
				systemAnalyzer, transformer);

		// add progress listener
		process.addListener(status -> {
			callGraphBuilded = true;
			dataContainer.setCallGraph(status);
		});

		// execute them
		executorService.submit(process);

		// no return
		return JsonUtil.emptyObject();
	}

	private JsonCallGraph convertCallGraphToJson(DirectedGraph<String, Integer> graph) {
		JsonCallGraph output = new JsonCallGraph();
		Repository parent = blackboard.getArchitectureModel().getRepository();

		for (String service : graph.getNodes()) {
			ResourceDemandingSEFF seff = PCMUtils.getElementById(parent, ResourceDemandingSEFF.class, service);
			JsonCallGraphNode node = new JsonCallGraphNode();
			node.setComponentId(seff.getBasicComponent_ServiceEffectSpecification().getId());
			node.setComponentName(seff.getBasicComponent_ServiceEffectSpecification().getEntityName());
			node.setServiceId(seff.getId());
			node.setServiceName(seff.getDescribedService__SEFF().getEntityName());

			output.getNodes().add(node);
		}

		for (Pair<String, String> edge : graph.getEdges()) {
			JsonCallGraphEdge nedge = new JsonCallGraphEdge();
			nedge.setServiceFrom(edge.getLeft());
			nedge.setServiceTo(edge.getRight());

			output.getEdges().add(nedge);
		}

		return output;
	}

}
