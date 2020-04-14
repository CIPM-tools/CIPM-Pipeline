package dmodel.pipeline.rt.validation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.core.facade.IRuntimeEnvironmentQueryFacade;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.rt.validation.data.metric.IValidationMetric;
import dmodel.pipeline.rt.validation.data.metric.impl.ServiceCallAverageDistanceAbsolute;
import dmodel.pipeline.rt.validation.data.metric.impl.ServiceCallAverageDistanceRelative;
import dmodel.pipeline.rt.validation.data.metric.impl.ServiceCallKSTestMetric;
import dmodel.pipeline.rt.validation.data.metric.impl.ServiceCallMedianDistanceAbsolute;
import dmodel.pipeline.rt.validation.data.metric.impl.ServiceCallQ1DistanceAbsolute;
import dmodel.pipeline.rt.validation.data.metric.impl.ServiceCallQ3DistanceAbsolute;
import dmodel.pipeline.rt.validation.data.metric.impl.ServiceCallWassersteinDistanceMetric;
import dmodel.pipeline.rt.validation.eval.MonitoringDataEnrichment;
import dmodel.pipeline.rt.validation.eval.ValidationDataExtractor;
import dmodel.pipeline.rt.validation.eval.util.PCMValidationPointMatcher;
import dmodel.pipeline.rt.validation.facade.RuntimeEnvironmentQueryImpl;
import dmodel.pipeline.rt.validation.simulation.HeadlessPCMSimulator;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.vsum.VsumManagerTestBase;
import dmodel.pipeline.vsum.facade.CentralVsumFacade;

@RunWith(SpringRunner.class)
public class ValidationProcessorTest extends VsumManagerTestBase {

	@TestConfiguration
	public static class TestContextConfiguration extends VsumManagerTestBase.VsumManagerTestConfiguration {

		@Bean
		public ValidationFeedbackComponent vfc() {
			return new ValidationFeedbackComponent();
		}

		@Bean
		public IRuntimeEnvironmentQueryFacade remFacade() {
			return new RuntimeEnvironmentQueryImpl();
		}

		@Bean
		public CentralVsumFacade vsumFacade() {
			return new CentralVsumFacade();
		}

		@Bean
		public PCMValidationPointMatcher valPointMatcher() {
			return new PCMValidationPointMatcher();
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
		public IValidationMetric<?> generator1() {
			return new ServiceCallKSTestMetric();
		}

		@Bean
		public IValidationMetric<?> generator2() {
			return new ServiceCallAverageDistanceAbsolute();
		}

		@Bean
		public IValidationMetric<?> generator3() {
			return new ServiceCallAverageDistanceRelative();
		}

		@Bean
		public IValidationMetric<?> generator4() {
			return new ServiceCallMedianDistanceAbsolute();
		}

		@Bean
		public IValidationMetric<?> generator5() {
			return new ServiceCallWassersteinDistanceMetric();
		}

		@Bean
		public IValidationMetric<?> generator6() {
			return new ServiceCallQ1DistanceAbsolute();
		}

		@Bean
		public IValidationMetric<?> generator7() {
			return new ServiceCallQ3DistanceAbsolute();
		}
	}

	@Autowired
	private ValidationFeedbackComponent feedback;

	@Autowired
	private IPcmModelProvider pcmProvider;

	@Test
	public void checkNotNull() {
		assertNotNull(feedback);
	}

	@Test
	public void testSimulation() {
		for (int i = 0; i < 25; i++) {
			ValidationData data = feedback.process(pcmProvider.getRaw(), Lists.newArrayList(), "TestAnalysis");
			assertNotNull(data);
			assertTrue(data.getValidationPoints().size() > 2);
		}
		feedback.clearSimulationData();
	}

	@Before
	public void loadPCMModels() {
		super.setSpecific(null, null, null);
		super.setPcm(
				ModelUtil.readFromResource(ValidationProcessorTest.class.getResource("/models/cocome.repository"),
						Repository.class),
				ModelUtil.readFromResource(ValidationProcessorTest.class.getResource("/models/cocome.system"),
						System.class),
				ModelUtil.readFromResource(
						ValidationProcessorTest.class.getResource("/models/cocome.resourceenvironment"),
						ResourceEnvironment.class),
				ModelUtil.readFromResource(ValidationProcessorTest.class.getResource("/models/cocome.allocation"),
						Allocation.class),
				ModelUtil.readFromResource(ValidationProcessorTest.class.getResource("/models/cocome.usagemodel"),
						UsageModel.class));
	}

}
