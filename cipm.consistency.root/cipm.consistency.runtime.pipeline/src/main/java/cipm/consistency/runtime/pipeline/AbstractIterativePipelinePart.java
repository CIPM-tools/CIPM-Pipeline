package cipm.consistency.runtime.pipeline;

import cipm.consistency.base.core.state.EPipelineTransformation;
import cipm.consistency.base.core.state.ETransformationState;
import cipm.consistency.base.core.state.ExecutionMeasuringPoint;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;

public abstract class AbstractIterativePipelinePart<B extends RuntimePipelineBlackboard> {
	private B blackboard;

	private ExecutionMeasuringPoint measuringPoint;
	private EPipelineTransformation transformation;

	public AbstractIterativePipelinePart(ExecutionMeasuringPoint measuringPoint,
			EPipelineTransformation transformation) {
		this.measuringPoint = measuringPoint;
		this.transformation = transformation;
	}

	protected void trackStart() {
		if (transformation != null) {
			blackboard.getQuery().updateState(transformation, ETransformationState.RUNNING);
		}

		if (measuringPoint != null) {
			blackboard.getQuery().track(measuringPoint);
		}
	}

	protected void trackEnd() {
		if (transformation != null) {
			blackboard.getQuery().updateState(transformation, ETransformationState.FINISHED);
		}

		if (measuringPoint != null) {
			blackboard.getQuery().track(measuringPoint);
		}
	}

	public B getBlackboard() {
		return blackboard;
	}

	public void setBlackboard(B blackboard) {
		this.blackboard = blackboard;
	}
}
