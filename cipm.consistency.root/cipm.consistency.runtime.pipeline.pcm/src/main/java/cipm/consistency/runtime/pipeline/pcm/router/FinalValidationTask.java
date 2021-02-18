package cipm.consistency.runtime.pipeline.pcm.router;

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
import cipm.consistency.runtime.pipeline.instrumentation.InstrumentationModelTransformation;
import lombok.extern.java.Log;

@Log
public class FinalValidationTask extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	public FinalValidationTask() {
		super(ExecutionMeasuringPoint.T_VALIDATION_4, EPipelineTransformation.T_VALIDATION3);
	}

	@InputPorts({ @InputPort(PortIDs.T_RAW_FINAL_VALIDATION), @InputPort(PortIDs.T_FINAL_VALIDATION) })
	@OutputPorts(@OutputPort(to = InstrumentationModelTransformation.class, id = PortIDs.T_FINAL_INM, async = false))
	public PartitionedMonitoringData<PCMContextRecord> validateFinal(PartitionedMonitoringData<PCMContextRecord> recs) {
		super.trackStart();

		log.info("Start simulation of the current models.");
		// simulate with validation data
		getBlackboard().getValidationQuery().process(getBlackboard().getPcmQuery().getRaw(), recs.getValidationData(),
				ValidationSchedulePoint.FINAL);

		// finish
		super.trackEnd();

		return recs;
	}

}
