package dmodel.pipeline.rt.router;

import java.util.List;

import dmodel.pipeline.monitoring.records.RecordWithSession;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.blackboard.state.EPipelineTransformation;
import dmodel.pipeline.rt.pipeline.blackboard.state.ETransformationState;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.shared.pipeline.PortIDs;
import lombok.extern.java.Log;

@Log
public class FinalValidationTask extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts({ @InputPort(PortIDs.T_RAW_FINAL_VALIDATION), @InputPort(PortIDs.T_FINAL_VALIDATION) })
	public void validateFinal(List<RecordWithSession> recs) {
		getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_VALIDATION3,
				ETransformationState.RUNNING);

		log.info("Start simulation of the current models.");
		// simulate
		ValidationData metrics = getBlackboard().getValidationFeedbackComponent().process(
				getBlackboard().getArchitectureModel(), getBlackboard().getBorder().getRuntimeMapping(), recs,
				"Pipeline-FinalValidation");

		// set results
		getBlackboard().getValidationResultContainer().setFinalResults(metrics);

		// finish
		getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_VALIDATION3,
				ETransformationState.FINISHED);
	}

}
