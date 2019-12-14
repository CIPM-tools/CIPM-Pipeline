package dmodel.pipeline.rt.validation;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import dmodel.pipeline.models.mapping.MappingFactory;
import dmodel.pipeline.rt.validation.data.metric.IValidationMetric;
import dmodel.pipeline.rt.validation.data.metric.impl.ServiceCallKSTestMetric;
import dmodel.pipeline.rt.validation.eval.MonitoringDataEnrichment;
import dmodel.pipeline.rt.validation.eval.ValidationDataExtractor;
import dmodel.pipeline.rt.validation.simulation.HeadlessPCMSimulator;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

@RunWith(SpringRunner.class)
public class ValidationProcessorTest {

	@Autowired
	private ValidationFeedbackComponent feedback;

	private InMemoryPCM pcm;

	@TestConfiguration
	static class EmployeeServiceImplTestContextConfiguration {

		@Bean
		public ValidationFeedbackComponent vfc() {
			return new ValidationFeedbackComponent();
		}

		@Bean
		public HeadlessPCMSimulator simulator() {
			return new HeadlessPCMSimulator();
		}

		@Bean
		public ValidationDataExtractor extractor() {
			return new ValidationDataExtractor();
		}

		@Bean
		public MonitoringDataEnrichment enricher() {
			return new MonitoringDataEnrichment();
		}

		@Bean
		public IValidationMetric<?> generator() {
			return new ServiceCallKSTestMetric();
		}

		@Bean
		public DModelConfigurationContainer configuration() {
			try {
				return new ObjectMapper(new YAMLFactory()).readValue(
						ValidationProcessorTest.class.getResourceAsStream("/defaultConfig.yml"),
						DModelConfigurationContainer.class);
			} catch (IOException e) {
				return null;
			}
		}
	}

	@Test
	public void checkNotNull() {
		assertNotNull(feedback);
	}

	@Test
	public void testSimulation() {
		feedback.process(pcm, MappingFactory.eINSTANCE.createPalladioRuntimeMapping(), Lists.newArrayList(),
				"TestAnalysis");
	}

	@Before
	public void loadPCMModels() {
		pcm = new InMemoryPCM();
		pcm.setRepository(ModelUtil.readFromResource(
				ValidationProcessorTest.class.getResource("/models/cocome.repository"), Repository.class));
		pcm.setSystem(ModelUtil.readFromResource(ValidationProcessorTest.class.getResource("/models/cocome.system"),
				System.class));
		pcm.setResourceEnvironmentModel(ModelUtil.readFromResource(
				ValidationProcessorTest.class.getResource("/models/cocome.resourceenvironment"),
				ResourceEnvironment.class));
		pcm.setAllocationModel(ModelUtil.readFromResource(
				ValidationProcessorTest.class.getResource("/models/cocome.allocation"), Allocation.class));
		pcm.setUsageModel(ModelUtil.readFromResource(
				ValidationProcessorTest.class.getResource("/models/cocome.usagemodel"), UsageModel.class));
	}

}
