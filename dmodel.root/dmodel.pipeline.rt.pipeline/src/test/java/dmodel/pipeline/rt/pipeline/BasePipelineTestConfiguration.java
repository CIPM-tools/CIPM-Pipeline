package dmodel.pipeline.rt.pipeline;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import dmodel.pipeline.core.facade.ICoreBlackboardQueryFacade;
import dmodel.pipeline.core.facade.IInstrumentationModelQueryFacade;
import dmodel.pipeline.core.facade.impl.InstrumentationModelQueryImpl;
import dmodel.pipeline.evaluation.PerformanceEvaluation;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.blackboard.facade.IValidationQueryFacade;
import dmodel.pipeline.rt.pipeline.blackboard.facade.IValidationResultsQuery;
import dmodel.pipeline.rt.pipeline.blackboard.facade.impl.CoreBlackboardQueryFacadeImpl;
import dmodel.pipeline.rt.pipeline.blackboard.facade.impl.ValidationQueryFacadeImpl;
import dmodel.pipeline.rt.pipeline.blackboard.facade.impl.ValidationResultsQueryImpl;
import dmodel.pipeline.rt.pipeline.blackboard.state.PipelineUIState;
import dmodel.pipeline.rt.pipeline.blackboard.validation.ValidationResultContainer;
import dmodel.pipeline.rt.validation.ValidationProcessorTest;
import dmodel.pipeline.rt.validation.simulation.HeadlessPCMSimulator;

public class BasePipelineTestConfiguration {

	@TestConfiguration
	public static class TestContextConfiguration extends ValidationProcessorTest.TestContextConfiguration {
		@Bean
		public RuntimePipelineBlackboard blackboard() {
			return new RuntimePipelineBlackboard();
		}

		@Bean
		public ICoreBlackboardQueryFacade blackboardQuery() {
			return new CoreBlackboardQueryFacadeImpl();
		}

		@Bean
		public PipelineUIState uiState() {
			return new PipelineUIState();
		}

		@Bean
		public PerformanceEvaluation performanceEval() {
			return new PerformanceEvaluation();
		}

		@Bean
		public IValidationQueryFacade validationFacade() {
			return new ValidationQueryFacadeImpl();
		}

		@Bean
		public HeadlessPCMSimulator headlessClient() {
			return new HeadlessPCMSimulator();
		}

		@Bean
		public ValidationResultContainer resultContainer() {
			return new ValidationResultContainer();
		}

		@Bean
		public IValidationResultsQuery resultQuery() {
			return new ValidationResultsQueryImpl();
		}

		@Bean
		public IInstrumentationModelQueryFacade inmQuery() {
			return new InstrumentationModelQueryImpl();
		}
	}

}
