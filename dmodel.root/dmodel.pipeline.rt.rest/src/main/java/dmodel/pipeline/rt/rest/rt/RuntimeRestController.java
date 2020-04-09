package dmodel.pipeline.rt.rest.rt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.core.facade.IInstrumentationModelQueryFacade;
import dmodel.pipeline.evaluation.PerformanceEvaluation;
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
	private IInstrumentationModelQueryFacade inmQueryFacade;

	@Autowired
	private PerformanceEvaluation performanceEvaluation;

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
			return objectMapper.writeValueAsString(inmQueryFacade.getInstrumentedActionIds());
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("error", true, false);
		}
	}

	@GetMapping("/runtime/pipeline/performance")
	public String performanceDetails() {
		log.fine("Performance details has been polled.");
		try {
			return objectMapper.writeValueAsString(performanceEvaluation.getData());
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("error", true, false);
		}
	}

}
