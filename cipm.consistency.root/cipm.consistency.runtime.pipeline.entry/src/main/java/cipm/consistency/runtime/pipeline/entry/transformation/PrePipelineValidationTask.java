package cipm.consistency.runtime.pipeline.entry.transformation;

import cipm.consistency.base.core.state.EPipelineTransformation;
import cipm.consistency.base.core.state.ExecutionMeasuringPoint;
import cipm.consistency.base.core.state.ValidationSchedulePoint;
import cipm.consistency.base.shared.pipeline.PortIDs;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.AbstractIterativePipelinePart;
import cipm.consistency.runtime.pipeline.annotation.InputPort;
import cipm.consistency.runtime.pipeline.annotation.InputPorts;
import cipm.consistency.runtime.pipeline.annotation.OutputPort;
import cipm.consistency.runtime.pipeline.annotation.OutputPorts;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.data.PartitionedMonitoringData;
import cipm.consistency.runtime.pipeline.pcm.router.FinalValidationTask;
import cipm.consistency.runtime.pipeline.pcm.util.ServiceCallTreeBuilder;
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
