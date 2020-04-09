package dmodel.pipeline.core.facade;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import dmodel.pipeline.core.state.EPipelineTransformation;
import dmodel.pipeline.core.state.ETransformationState;

public interface ICoreBlackboardQueryFacade extends IResettableQueryFacade {

	public void updateState(EPipelineTransformation transformation, ETransformationState state);

	// performance tracking events
	public void trackStartPipelineExecution();

	public void trackEndPipelineExecution();

	public void track(ExecutionMeasuringPoint point);

	public void trackRecordCount(int count);

	public void trackPath(boolean b);

	public void trackUsageScenarios(int scenarioCount);

}
