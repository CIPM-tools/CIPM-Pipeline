package cipm.consistency.runtime.pipeline.blackboard.facade.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import cipm.consistency.base.core.facade.IResettableQueryFacade;
import cipm.consistency.base.core.health.HealthStateManager;
import cipm.consistency.base.core.health.HealthStateObservedComponent;
import cipm.consistency.base.core.health.HealthStateProblem;
import cipm.consistency.base.core.health.HealthStateProblemSeverity;
import cipm.consistency.base.core.state.ValidationSchedulePoint;
import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.blackboard.facade.IValidationQueryFacade;
import cipm.consistency.runtime.pipeline.blackboard.validation.ValidationResultContainer;
import cipm.consistency.runtime.pipeline.validation.IValidationProcessor;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;
import cipm.consistency.runtime.pipeline.validation.data.ValidationMetricValue;
import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;
import cipm.consistency.runtime.pipeline.validation.data.metric.ValidationMetricType;

@Component
public class ValidationQueryFacadeImpl implements IValidationQueryFacade, IResettableQueryFacade {
	@Autowired
	private IValidationProcessor validationComponent;

	@Autowired
	private ValidationResultContainer resultContainer;

	@Autowired
	private HealthStateManager healthStateManager;

	private Map<ValidationSchedulePoint, Long> problemMapping;

	public ValidationQueryFacadeImpl() {
		this.problemMapping = Maps.newHashMap();
	}

	@Override
	public void process(InMemoryPCM raw, List<PCMContextRecord> data, ValidationSchedulePoint schedulePoint) {
		ValidationData result = validationComponent.process(raw, data, schedulePoint.getName());
		resultContainer.setData(schedulePoint, result);

		if (result.isEmpty()) {
			if (!problemMapping.containsKey(schedulePoint)) {
				problemMapping.put(schedulePoint,
						healthStateManager.addProblem(HealthStateProblem.builder()
								.description("Failed to simulate the architecture model (at '" + schedulePoint + "').")
								.severity(HealthStateProblemSeverity.WARNING)
								.source(HealthStateObservedComponent.PIPELINE).build()));
			}
		} else {
			if (problemMapping.containsKey(schedulePoint)) {
				healthStateManager.removeProblem(problemMapping.get(schedulePoint));
				problemMapping.remove(schedulePoint);
			}

			// vis calculation
			if (schedulePoint != ValidationSchedulePoint.PRE_PIPELINE) {
				result.calculateImprovmentScore(resultContainer.getData(ValidationSchedulePoint.PRE_PIPELINE));
			}
		}
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

	@Override
	public void reset(boolean hard) {
		if (hard) {
			validationComponent.clearSimulationData();
		}
	}

}
