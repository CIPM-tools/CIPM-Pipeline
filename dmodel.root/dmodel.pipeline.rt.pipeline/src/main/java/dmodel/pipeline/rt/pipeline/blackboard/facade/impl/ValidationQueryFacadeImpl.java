package dmodel.pipeline.rt.pipeline.blackboard.facade.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

import dmodel.pipeline.core.validation.ValidationSchedulePoint;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.pipeline.blackboard.facade.IValidationQueryFacade;
import dmodel.pipeline.rt.pipeline.blackboard.validation.ValidationResultContainer;
import dmodel.pipeline.rt.validation.IValidationProcessor;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.rt.validation.data.ValidationMetricValue;
import dmodel.pipeline.rt.validation.data.ValidationPoint;
import dmodel.pipeline.rt.validation.data.metric.ValidationMetricType;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

public class ValidationQueryFacadeImpl implements IValidationQueryFacade {
	@Autowired
	private IValidationProcessor validationComponent;

	@Autowired
	private ValidationResultContainer resultContainer;

	@Override
	public void process(InMemoryPCM raw, List<PCMContextRecord> data, ValidationSchedulePoint schedulePoint) {
		ValidationData result = validationComponent.process(raw, data, schedulePoint.getName());
		resultContainer.setData(schedulePoint, result);
	}

	@Override
	public int compare(ValidationData o1, ValidationData o2) {
		int sum = 0;
		Map<Triple<String, String, ValidationMetricType>, ValidationMetricValue> mappingA = Maps.newHashMap();
		if (o1 != null && o2 != null && !o1.isEmpty() && !o2.isEmpty()) {
			o1.getValidationPoints().forEach(m -> {
				m.getMetricValues().forEach(metricValue -> {
					mappingA.put(Triple.of(m.getId(), m.getMetricDescription().getId(), metricValue.type()),
							metricValue);
				});
			});
			for (ValidationPoint validationPoint : o2.getValidationPoints()) {
				for (ValidationMetricValue val : validationPoint.getMetricValues()) {
					Triple<String, String, ValidationMetricType> query = Triple.of(validationPoint.getId(),
							validationPoint.getMetricDescription().getId(), val.type());
					if (mappingA.containsKey(query)) {
						double comp = mappingA.get(query).compare(val);
						if (comp > 0) {
							sum += 1;
						} else if (comp < 0) {
							sum -= 1;
						}
					}
				}
			}
		}

		if (sum == 0) {
			return 0;
		} else if (sum > 0) {
			return 1;
		} else {
			return -1;
		}
	}

}
