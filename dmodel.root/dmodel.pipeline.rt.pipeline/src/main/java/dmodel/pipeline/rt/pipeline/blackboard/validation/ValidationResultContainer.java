package dmodel.pipeline.rt.pipeline.blackboard.validation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import dmodel.pipeline.core.validation.ValidationSchedulePoint;
import dmodel.pipeline.rt.validation.data.ValidationData;

@Service
public class ValidationResultContainer {
	private Map<ValidationSchedulePoint, ValidationData> dataContainer;

	public ValidationResultContainer() {
		dataContainer = Maps.newHashMap();
	}

	public ValidationData getData(ValidationSchedulePoint point) {
		return dataContainer.get(point);
	}

	public boolean hasData(ValidationSchedulePoint point) {
		return dataContainer.containsKey(point);
	}

	public List<ValidationSchedulePoint> getExistingPoints() {
		return Stream.of(ValidationSchedulePoint.values()).filter(v -> hasData(v)).collect(Collectors.toList());
	}

	public void setData(ValidationSchedulePoint point, ValidationData data) {
		if (point != ValidationSchedulePoint.EXTRAORDINARY) {
			dataContainer.put(point, data);
		}
	}

	public void reset() {
		dataContainer.clear();
	}

}
