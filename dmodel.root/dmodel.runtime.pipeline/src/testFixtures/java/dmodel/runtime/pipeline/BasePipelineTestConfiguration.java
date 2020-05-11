package dmodel.runtime.pipeline;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import dmodel.base.core.facade.ICoreBlackboardQueryFacade;
import dmodel.base.core.facade.IInstrumentationModelQueryFacade;
import dmodel.base.core.facade.impl.InstrumentationModelQueryImpl;
import dmodel.base.evaluation.PerformanceEvaluation;
import dmodel.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.runtime.pipeline.blackboard.facade.IPipelineHealthQueryFacade;
import dmodel.runtime.pipeline.blackboard.facade.IValidationQueryFacade;
import dmodel.runtime.pipeline.blackboard.facade.IValidationResultsQuery;
import dmodel.runtime.pipeline.blackboard.facade.impl.CoreBlackboardQueryFacadeImpl;
import dmodel.runtime.pipeline.blackboard.facade.impl.PipelineHealthQueryFacadeImpl;
import dmodel.runtime.pipeline.blackboard.facade.impl.ValidationQueryFacadeImpl;
import dmodel.runtime.pipeline.blackboard.facade.impl.ValidationResultsQueryImpl;
import dmodel.runtime.pipeline.blackboard.state.PipelineUIState;
import dmodel.runtime.pipeline.blackboard.validation.ValidationResultContainer;
import dmodel.runtime.pipeline.validation.ValidationProcessorTestBase;
import dmodel.runtime.pipeline.validation.simulation.HeadlessPCMSimulator;

public class BasePipelineTestConfiguration {

	@TestConfiguration
	public static class TestContextConfiguration extends ValidationProcessorTestBase.TestContextConfiguration {
		@Bean
		public IPipelineHealthQueryFacade healthQueryFacade() {
			return new PipelineHealthQueryFacadeImpl();
		}

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
