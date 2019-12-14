package dmodel.pipeline.rt.rest.rt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.rt.pipeline.blackboard.state.PipelineUIState;
import dmodel.pipeline.shared.JsonUtil;

@RestController
public class RuntimeRestController {
	@Autowired
	private PipelineUIState pipelineState;

	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping("/runtime/pipeline/status")
	public String instrumentationStatus() {
		try {
			return objectMapper.writeValueAsString(pipelineState);
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("error", true, false);
		}
	}

}
