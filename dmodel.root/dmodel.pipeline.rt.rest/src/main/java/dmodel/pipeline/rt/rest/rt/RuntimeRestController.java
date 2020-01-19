package dmodel.pipeline.rt.rest.rt;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.blackboard.state.PipelineUIState;
import dmodel.pipeline.shared.JsonUtil;
import lombok.extern.java.Log;

@RestController
@Log
public class RuntimeRestController {
	@Autowired
	private PipelineUIState pipelineState;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@GetMapping("/runtime/pipeline/status")
	public String pipelineStatus() {
		try {
			return objectMapper.writeValueAsString(pipelineState);
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("error", true, false);
		}
	}

	@GetMapping("/runtime/pipeline/imm")
	public String instrumentationModel() {
		log.fine("Instrumentation model has been polled.");
		try {
			return objectMapper
					.writeValueAsString(blackboard.getInstrumentationModel().getPoints().stream().map(sip -> {
						if (sip.isActive()) {
							Set<String> inner = Sets.newHashSet(sip.getService().getId());
							sip.getActionInstrumentationPoints().stream().forEach(aip -> {
								if (aip.isActive()) {
									inner.add(aip.getAction().getId());
								}
							});
							return inner;
						} else {
							return Sets.newHashSet();
						}
					}).flatMap(Set::stream).collect(Collectors.toList()));
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("error", true, false);
		}
	}

	@GetMapping("/runtime/pipeline/performance")
	public String performanceDetails() {
		log.fine("Performance details has been polled.");
		try {
			return objectMapper.writeValueAsString(blackboard.getPerformanceEvaluation().getData());
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("error", true, false);
		}
	}

}
