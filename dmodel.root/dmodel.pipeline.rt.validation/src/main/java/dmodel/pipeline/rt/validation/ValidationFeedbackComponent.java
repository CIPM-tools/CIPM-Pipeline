package dmodel.pipeline.rt.validation;

import java.util.List;

import org.pcm.headless.shared.data.results.InMemoryResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dmodel.pipeline.core.health.AbstractHealthStateComponent;
import dmodel.pipeline.core.health.HealthState;
import dmodel.pipeline.core.health.HealthStateObservedComponent;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.rt.validation.data.ValidationMetricValue;
import dmodel.pipeline.rt.validation.data.metric.IValidationMetric;
import dmodel.pipeline.rt.validation.eval.ValidationDataExtractor;
import dmodel.pipeline.rt.validation.simulation.HeadlessPCMSimulator;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

@Component
public class ValidationFeedbackComponent extends AbstractHealthStateComponent implements IValidationProcessor {
	@Autowired
	private HeadlessPCMSimulator simulator;

	@Autowired
	private ValidationDataExtractor extractor;

	@Autowired
	private List<IValidationMetric<?>> metrics;

	private boolean workingBefore = false;

	public ValidationFeedbackComponent() {
		super(HealthStateObservedComponent.VALIDATION_CONTROLLER, HealthStateObservedComponent.CONFIGURATION);
	}

	@Scheduled(fixedRate = 10000L, initialDelay = 0)
	public void checkAvailability() {
		if (checkPreconditions()) {
			if (simulator.isReachable() && !workingBefore) {
				super.removeAllProblems();
				super.updateState();
				workingBefore = true;
			} else if (workingBefore) {
				super.reportError("Headless PCM simulator is not reachable. Please check your configuration.");
				super.updateState();
				workingBefore = false;
			}
		}
	}

	@Override
	public ValidationData process(InMemoryPCM instance, List<PCMContextRecord> monitoringData, String taskName) {
		// 1. simulate it
		InMemoryResultRepository analysisResults = simulator.simulateBlocking(instance, taskName);
		if (analysisResults == null) {
			return new ValidationData();
		}

		// 2. enrich with data
		ValidationData preparedData = extractor.extractValidationData(analysisResults, instance, monitoringData);

		// 3. derive metrics
		preparedData.getValidationPoints().stream().forEach(valPoint -> {
			metrics.forEach(metric -> {
				if (metric.isTarget(valPoint)) {
					ValidationMetricValue result = metric.calculate(valPoint);
					if (result != null)
						valPoint.getMetricValues().add(result);
				}
			});
		});

		return preparedData;
	}

	public void clearSimulationData() {
		simulator.clearAllSimulationData();
	}

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		// nothing to do here because no deps
	}
}