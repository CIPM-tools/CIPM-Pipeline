package cipm.consistency.runtime.pipeline;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import cipm.consistency.base.core.facade.ICoreBlackboardQueryFacade;
import cipm.consistency.base.core.facade.IInstrumentationModelQueryFacade;
import cipm.consistency.base.core.facade.impl.InstrumentationModelQueryImpl;
import cipm.consistency.base.evaluation.PerformanceEvaluation;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.blackboard.facade.IPipelineHealthQueryFacade;
import cipm.consistency.runtime.pipeline.blackboard.facade.IValidationQueryFacade;
import cipm.consistency.runtime.pipeline.blackboard.facade.IValidationResultsQuery;
import cipm.consistency.runtime.pipeline.blackboard.facade.impl.CoreBlackboardQueryFacadeImpl;
import cipm.consistency.runtime.pipeline.blackboard.facade.impl.PipelineHealthQueryFacadeImpl;
import cipm.consistency.runtime.pipeline.blackboard.facade.impl.ValidationQueryFacadeImpl;
import cipm.consistency.runtime.pipeline.blackboard.facade.impl.ValidationResultsQueryImpl;
import cipm.consistency.runtime.pipeline.blackboard.state.PipelineUIState;
import cipm.consistency.runtime.pipeline.blackboard.validation.ValidationResultContainer;
import cipm.consistency.runtime.pipeline.validation.ValidationProcessorTestBase;
import cipm.consistency.runtime.pipeline.validation.simulation.HeadlessPCMSimulator;

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
