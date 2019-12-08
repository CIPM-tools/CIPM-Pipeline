package dmodel.pipeline.rt.validation;

import java.util.List;
import java.util.stream.Collectors;

import org.pcm.headless.shared.data.results.InMemoryResultRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.monitoring.records.ServiceContextRecord;
import dmodel.pipeline.rt.validation.data.ValidationMetric;
import dmodel.pipeline.rt.validation.eval.IAnalysisDataProcessor;
import dmodel.pipeline.rt.validation.eval.IMonitoringDataProcessor;
import dmodel.pipeline.rt.validation.eval.IValidationDataGenerator;
import dmodel.pipeline.rt.validation.simulation.HeadlessPCMSimulator;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

@Component
public class ValidationFeedbackComponent implements IValidationProcessor, InitializingBean {
	@Autowired
	private DModelConfigurationContainer config;

	@Autowired
	private HeadlessPCMSimulator simulator;

	@Autowired
	private List<IValidationDataGenerator> validationDataGenerators;

	@Autowired
	private List<IMonitoringDataProcessor> monitoringDataProcessors;

	@Autowired
	private List<IAnalysisDataProcessor> analysisDataProcessors;

	public ValidationFeedbackComponent() {
	}

	@Override
	public List<ValidationMetric> process(InMemoryPCM instance, List<ServiceContextRecord> monitoringData,
			String taskName) {
		// 1. simulate it
		InMemoryResultRepository analysisResults = simulator.simulateBlocking(instance, taskName);

		// 2. execute all parts that evaluate the validation
		validationDataGenerators.forEach(vdg -> vdg.clearValidationMetrics());
		if (analysisResults != null) {
			monitoringDataProcessors.forEach(mdp -> monitoringData.forEach(md -> mdp.processMonitoringRecord(md)));
			analysisDataProcessors.forEach(adp -> adp.processAnalysisData(analysisResults));
		}

		// 3. get all
		return validationDataGenerators.stream().map(vdg -> vdg.getValidationMetrics()).flatMap(List::stream)
				.collect(Collectors.toList());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}
}