package cipm.consistency.app.rest.rt.validation;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cipm.consistency.app.rest.rt.validation.data.JsonValidationOverview;
import cipm.consistency.app.rest.rt.validation.data.JsonValidationPointOverview;
import cipm.consistency.base.core.state.ValidationSchedulePoint;
import cipm.consistency.base.shared.JsonUtil;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;
import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;
import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
public class ValidationRestController {

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping("/runtime/validation/overview")
	public String validationOverview() {
		JsonValidationOverview overviewResult = new JsonValidationOverview();

		for (ValidationSchedulePoint schedulePoint : ValidationSchedulePoint.values()) {
			ValidationData correspondingData = blackboard.getValidationResultsQuery().get(schedulePoint);

			if (correspondingData != null) {
				JsonValidationPointOverview point = new JsonValidationPointOverview();
				point.setEmpty(correspondingData.isEmpty());
				point.setValidationDescription(schedulePoint.getName());
				point.setValidationDescriptionEnum(schedulePoint.toString());
				if (!correspondingData.isEmpty()) {
					point.setValidationPointCount(correspondingData.getValidationPoints().size() > 0
							? correspondingData.getValidationPoints().size()
							: 0);
					point.setVisPresent(correspondingData.getValidationImprovementScore().isPresent());
					
					if (point.isVisPresent()) {
						point.setValidationImprovementScore(correspondingData.getValidationImprovementScore().get());
					}
				}

				overviewResult.getPoints().add(point);
			}
		}

		try {
			return objectMapper.writeValueAsString(overviewResult);
		} catch (JsonProcessingException e) {
			return JsonUtil.emptyObject();
		}
	}

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
		ValidationSchedulePoint point = ValidationSchedulePoint.valueOf(type);
		if (point != null) {
			return blackboard.getValidationResultsQuery().get(point);
		} else {
			return new ValidationData(); // = empty
		}
	}

}
