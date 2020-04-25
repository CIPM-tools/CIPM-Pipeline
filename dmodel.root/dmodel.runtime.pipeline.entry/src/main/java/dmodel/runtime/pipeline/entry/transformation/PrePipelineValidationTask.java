package dmodel.runtime.pipeline.entry.transformation;

import dmodel.base.core.evaluation.ExecutionMeasuringPoint;
import dmodel.base.core.state.EPipelineTransformation;
import dmodel.base.core.validation.ValidationSchedulePoint;
import dmodel.base.shared.pipeline.PortIDs;
import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.runtime.pipeline.AbstractIterativePipelinePart;
import dmodel.runtime.pipeline.annotation.InputPort;
import dmodel.runtime.pipeline.annotation.InputPorts;
import dmodel.runtime.pipeline.annotation.OutputPort;
import dmodel.runtime.pipeline.annotation.OutputPorts;
import dmodel.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.runtime.pipeline.data.PartitionedMonitoringData;
import dmodel.runtime.pipeline.pcm.router.FinalValidationTask;
import lombok.extern.java.Log;

@Log
public class PrePipelineValidationTask extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	public PrePipelineValidationTask() {
		super(ExecutionMeasuringPoint.T_VALIDATION_1, EPipelineTransformation.PRE_VALIDATION);
	}

	@InputPorts({ @InputPort(PortIDs.T_VAL_PRE) })
	@OutputPorts({ @OutputPort(to = ServiceCallTreeBuilder.class, id = PortIDs.T_BUILD_SERVICECALL_TREE, async = false),
			@OutputPort(to = FinalValidationTask.class, id = PortIDs.T_RAW_FINAL_VALIDATION, async = false) })
	public PartitionedMonitoringData<PCMContextRecord> prePipelineValidation(
			PartitionedMonitoringData<PCMContextRecord> recs) {
		super.trackStart();

		log.info("Start simulation of the current models.");
		// simulate using all monitoring data
		getBlackboard().getValidationQuery().process(getBlackboard().getPcmQuery().getRaw(), recs.getAllData(),
				ValidationSchedulePoint.PRE_PIPELINE);

		// finish
		super.trackEnd();

		// pass data
		return recs;
	}

}
