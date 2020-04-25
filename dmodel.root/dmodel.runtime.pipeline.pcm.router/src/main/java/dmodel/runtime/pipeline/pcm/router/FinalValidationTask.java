package dmodel.runtime.pipeline.pcm.router;

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
import dmodel.runtime.pipeline.inm.transformation.InstrumentationModelTransformation;
import dmodel.runtime.pipeline.validation.data.ValidationData;
import lombok.extern.java.Log;

@Log
public class FinalValidationTask extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	public FinalValidationTask() {
		super(ExecutionMeasuringPoint.T_VALIDATION_4, EPipelineTransformation.T_VALIDATION3);
	}

	@InputPorts({ @InputPort(PortIDs.T_RAW_FINAL_VALIDATION), @InputPort(PortIDs.T_FINAL_VALIDATION) })
	@OutputPorts({ @OutputPort(id = PortIDs.T_VAL_IMM, async = false, to = InstrumentationModelTransformation.class) })
	public ValidationData validateFinal(PartitionedMonitoringData<PCMContextRecord> recs) {
		super.trackStart();

		log.info("Start simulation of the current models.");
		// simulate with validation data
		getBlackboard().getValidationQuery().process(getBlackboard().getPcmQuery().getRaw(), recs.getValidationData(),
				ValidationSchedulePoint.FINAL);

		// finish
		super.trackEnd();

		return getBlackboard().getValidationResultsQuery().get(ValidationSchedulePoint.FINAL);
	}

}
