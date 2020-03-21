package dmodel.pipeline.rt.router;

import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.imm.transformation.InstrumentationModelTransformation;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.blackboard.state.EPipelineTransformation;
import dmodel.pipeline.rt.pipeline.blackboard.state.ETransformationState;
import dmodel.pipeline.rt.pipeline.data.PartitionedMonitoringData;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.shared.pipeline.PortIDs;
import lombok.extern.java.Log;

@Log
public class FinalValidationTask extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts({ @InputPort(PortIDs.T_RAW_FINAL_VALIDATION), @InputPort(PortIDs.T_FINAL_VALIDATION) })
	@OutputPorts({ @OutputPort(id = PortIDs.T_VAL_IMM, async = false, to = InstrumentationModelTransformation.class) })
	public ValidationData validateFinal(PartitionedMonitoringData<PCMContextRecord> recs) {
		getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_VALIDATION3,
				ETransformationState.RUNNING);
		long start = getBlackboard().getPerformanceEvaluation().getTime();

		log.info("Start simulation of the current models.");
		// simulate with validation data
		ValidationData metrics = getBlackboard().getValidationFeedbackComponent().process(
				getBlackboard().getArchitectureModel(), getBlackboard().getBorder().getRuntimeMapping(),
				recs.getValidationData(), "Pipeline-FinalValidation");

		// set results
		getBlackboard().getValidationResultContainer().setFinalResults(metrics);

		// finish
		getBlackboard().getPerformanceEvaluation().trackFinalValidation(start);
		getBlackboard().getPipelineState().updateState(EPipelineTransformation.T_VALIDATION3,
				ETransformationState.FINISHED);

		return metrics;
	}

}
