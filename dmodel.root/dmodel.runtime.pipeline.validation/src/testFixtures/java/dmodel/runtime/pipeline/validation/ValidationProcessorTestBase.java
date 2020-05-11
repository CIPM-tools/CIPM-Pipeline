package dmodel.runtime.pipeline.validation;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import dmodel.base.core.facade.IRuntimeEnvironmentQueryFacade;
import dmodel.base.vsum.VsumManagerTestBase;
import dmodel.base.vsum.facade.CentralVsumFacade;
import dmodel.runtime.pipeline.validation.data.metric.IValidationMetric;
import dmodel.runtime.pipeline.validation.data.metric.impl.ServiceCallAverageDistanceAbsolute;
import dmodel.runtime.pipeline.validation.data.metric.impl.ServiceCallAverageDistanceRelative;
import dmodel.runtime.pipeline.validation.data.metric.impl.ServiceCallKSTestMetric;
import dmodel.runtime.pipeline.validation.data.metric.impl.ServiceCallMedianDistanceAbsolute;
import dmodel.runtime.pipeline.validation.data.metric.impl.ServiceCallQ1DistanceAbsolute;
import dmodel.runtime.pipeline.validation.data.metric.impl.ServiceCallQ3DistanceAbsolute;
import dmodel.runtime.pipeline.validation.data.metric.impl.ServiceCallWassersteinDistanceMetric;
import dmodel.runtime.pipeline.validation.eval.MonitoringDataEnrichment;
import dmodel.runtime.pipeline.validation.eval.ValidationDataExtractor;
import dmodel.runtime.pipeline.validation.eval.util.PCMValidationPointMatcher;
import dmodel.runtime.pipeline.validation.facade.RuntimeEnvironmentQueryImpl;
import dmodel.runtime.pipeline.validation.simulation.HeadlessPCMSimulator;

public class ValidationProcessorTestBase {

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

}
