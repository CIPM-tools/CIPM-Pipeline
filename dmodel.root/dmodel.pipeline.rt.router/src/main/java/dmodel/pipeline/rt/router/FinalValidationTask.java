package dmodel.pipeline.rt.router;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import dmodel.pipeline.core.state.EPipelineTransformation;
import dmodel.pipeline.core.state.ETransformationState;
import dmodel.pipeline.core.validation.ValidationSchedulePoint;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.imm.transformation.InstrumentationModelTransformation;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.data.PartitionedMonitoringData;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.shared.pipeline.PortIDs;
import lombok.extern.java.Log;

@Log
public class FinalValidationTask extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts({ @InputPort(PortIDs.T_RAW_FINAL_VALIDATION), @InputPort(PortIDs.T_FINAL_VALIDATION) })
	@OutputPorts({ @OutputPort(id = PortIDs.T_VAL_IMM, async = false, to = InstrumentationModelTransformation.class) })
	public ValidationData validateFinal(PartitionedMonitoringData<PCMContextRecord> recs) {
		getBlackboard().getQuery().updateState(EPipelineTransformation.T_VALIDATION3, ETransformationState.RUNNING);
		getBlackboard().getQuery().track(ExecutionMeasuringPoint.T_VALIDATION_4);

		log.info("Start simulation of the current models.");
		// simulate with validation data
		getBlackboard().getValidationQuery().process(getBlackboard().getPcmQuery().getRaw(), recs.getValidationData(),
				ValidationSchedulePoint.FINAL);

		// finish
		getBlackboard().getQuery().track(ExecutionMeasuringPoint.T_VALIDATION_4);
		getBlackboard().getQuery().updateState(EPipelineTransformation.T_VALIDATION3, ETransformationState.FINISHED);

		return getBlackboard().getValidationResultsQuery().get(ValidationSchedulePoint.FINAL);
	}

}
