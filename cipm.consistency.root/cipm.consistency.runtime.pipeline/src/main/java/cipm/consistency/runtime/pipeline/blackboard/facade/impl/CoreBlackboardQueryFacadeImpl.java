package cipm.consistency.runtime.pipeline.blackboard.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cipm.consistency.base.core.facade.ICoreBlackboardQueryFacade;
import cipm.consistency.base.core.health.HealthState;
import cipm.consistency.base.core.state.EPipelineTransformation;
import cipm.consistency.base.core.state.ETransformationState;
import cipm.consistency.base.core.state.ExecutionMeasuringPoint;
import cipm.consistency.base.evaluation.PerformanceEvaluation;
import cipm.consistency.runtime.pipeline.blackboard.state.PipelineUIState;

@Component
public class CoreBlackboardQueryFacadeImpl implements ICoreBlackboardQueryFacade {
	@Autowired
	private PipelineUIState uiState;

	@Autowired
	private PerformanceEvaluation performanceEvaluation;

	@Override
	public void reset(boolean hard) {
		if (hard) {
			uiState.reset();
		}
	}

	@Override
	public void updateState(EPipelineTransformation transformation, ETransformationState state) {
		uiState.updateState(transformation, state);
	}

	@Override
	public void trackStartPipelineExecution() {
		performanceEvaluation.enterPipelineExecution();
	}

	@Override
	public void trackEndPipelineExecution(HealthState success) {
		performanceEvaluation.exitPipelineExecution(success);
	}

	@Override
	public void track(ExecutionMeasuringPoint point) {
		performanceEvaluation.trackMeasuringPoint(point);
	}

	@Override
	public void trackRecordCount(int count) {
		performanceEvaluation.trackRecordCount(count);
	}

	@Override
	public void trackPath(boolean b) {
		performanceEvaluation.trackPath(b);
	}

	@Override
	public void trackUsageScenarios(int scenarioCount) {
		performanceEvaluation.trackUsageScenarios(scenarioCount);
	}

}
