package cipm.consistency.app.rest.rt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cipm.consistency.base.core.facade.IInstrumentationModelQueryFacade;
import cipm.consistency.base.evaluation.PerformanceEvaluation;
import cipm.consistency.base.evaluation.overhead.MonitoringOverheadData;
import cipm.consistency.base.evaluation.overhead.MonitoringOverheadEvaluation;
import cipm.consistency.base.shared.JsonUtil;
import cipm.consistency.runtime.pipeline.blackboard.state.PipelineUIState;
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

	@Autowired
	private MonitoringOverheadEvaluation overheadEvaluation;

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
		log.fine("Performance details have been polled.");
		try {
			return objectMapper.writeValueAsString(performanceEvaluation.getData());
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("error", true, false);
		}
	}

	@PostMapping("/runtime/pipeline/overhead")
	public String registerOverhead(@RequestBody String overheadStructure) {
		try {
			MonitoringOverheadData result = objectMapper.readValue(overheadStructure, MonitoringOverheadData.class);
			overheadEvaluation.getContainer().add(result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return JsonUtil.emptyObject();
	}

}
