package dmodel.pipeline.rt.pcm.validation;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import dmodel.pipeline.core.state.EPipelineTransformation;
import dmodel.pipeline.core.state.ETransformationState;
import dmodel.pipeline.core.validation.ValidationSchedulePoint;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.data.PartitionedMonitoringData;
import dmodel.pipeline.rt.router.FinalValidationTask;
import dmodel.pipeline.shared.pipeline.PortIDs;
import lombok.extern.java.Log;

@Log
public class PrePipelineValidationTask extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@InputPorts({ @InputPort(PortIDs.T_VAL_PRE) })
	@OutputPorts({ @OutputPort(to = ServiceCallTreeBuilder.class, id = PortIDs.T_BUILD_SERVICECALL_TREE, async = false),
			@OutputPort(to = FinalValidationTask.class, id = PortIDs.T_RAW_FINAL_VALIDATION, async = false) })
	public PartitionedMonitoringData<PCMContextRecord> prePipelineValidation(
			PartitionedMonitoringData<PCMContextRecord> recs) {
		getBlackboard().getQuery().updateState(EPipelineTransformation.PRE_VALIDATION, ETransformationState.RUNNING);
		getBlackboard().getQuery().track(ExecutionMeasuringPoint.T_VALIDATION_1);

		log.info("Start simulation of the current models.");
		// simulate using all monitoring data
		getBlackboard().getValidationQuery().process(getBlackboard().getPcmQuery().getRaw(), recs.getAllData(),
				ValidationSchedulePoint.PRE_PIPELINE);

		// finish
		getBlackboard().getQuery().track(ExecutionMeasuringPoint.T_VALIDATION_1);
		getBlackboard().getQuery().updateState(EPipelineTransformation.PRE_VALIDATION, ETransformationState.FINISHED);

		// pass data
		return recs;
	}

}
