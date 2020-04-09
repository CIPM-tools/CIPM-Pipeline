package dmodel.pipeline.rt.rest.rt.validation;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.core.validation.ValidationSchedulePoint;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.rest.rt.validation.data.PipelineValidationPoint;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.rt.validation.data.ValidationPoint;
import dmodel.pipeline.shared.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
public class ValidationRestController {

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping("/runtime/validation/points")
	public String validationPoints(@RequestParam(value = "data") String type) {
		ValidationData data = getValidationDataFromPoint(type);

		if (data == null) {
			return JsonUtil.emptyArray();
		}

		try {
			return objectMapper.writeValueAsString(data.getValidationPoints().stream()
					.map(v -> new WrappedIdNamePair(v.getMeasuringPoint().getStringRepresentation(), v.getId()))
					.collect(Collectors.toList()));
		} catch (JsonProcessingException e) {
			return JsonUtil.emptyArray();
		}
	}

	@GetMapping("/runtime/validation/data")
	public String retrieveData(@RequestParam String id, @RequestParam String container) {
		ValidationData data = getValidationDataFromPoint(container);
		if (data != null) {
			ValidationPoint belPoint = data.getValidationPoints().stream().filter(v -> v.getId().equals(id)).findFirst()
					.orElse(null);
			if (belPoint != null) {
				try {
					return objectMapper.writeValueAsString(belPoint);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					return JsonUtil.emptyObject();
				}
			}
		}
		return JsonUtil.emptyObject();
	}

	@Data
	@AllArgsConstructor
	private static class WrappedIdNamePair {
		private String name;
		private String id;
	}

	private ValidationData getValidationDataFromPoint(String type) {
		PipelineValidationPoint point = PipelineValidationPoint.fromString(type);

		ValidationData data;
		switch (point) {
		case AFTER_REPO:
			data = blackboard.getValidationResultsQuery().get(ValidationSchedulePoint.AFTER_T_REPO);
			break;
		case AFTER_USAGE:
			data = blackboard.getValidationResultsQuery().get(ValidationSchedulePoint.AFTER_T_USAGE);
			break;
		case FINAL:
			data = blackboard.getValidationResultsQuery().get(ValidationSchedulePoint.FINAL);
			break;
		case PRE:
			data = blackboard.getValidationResultsQuery().get(ValidationSchedulePoint.PRE_PIPELINE);
			break;
		default:
			return null;
		}
		return data;
	}

}
