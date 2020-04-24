package dmodel.pipeline.rt.entry.core;

import java.util.List;
import java.util.stream.Collectors;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.pcm.validation.PrePipelineValidationTask;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.EntryInputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.annotation.PipelineEntryPoint;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.pipeline.data.PCMPartionedMonitoringData;
import dmodel.pipeline.rt.pipeline.data.PartitionedMonitoringData;
import dmodel.pipeline.rt.router.AccuracySwitch;
import dmodel.pipeline.shared.pipeline.PortIDs;
import kieker.common.record.IMonitoringRecord;
import lombok.extern.java.Log;

@PipelineEntryPoint
@Log
public class IterativePipelineEntryPoint extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	public IterativePipelineEntryPoint() {
		super(ExecutionMeasuringPoint.T_PRE_FILTER, null);
	}

	@EntryInputPort
	@OutputPorts({ @OutputPort(id = PortIDs.T_VAL_PRE, to = PrePipelineValidationTask.class, async = false),
			@OutputPort(id = PortIDs.T_RAW_ROUTER, to = AccuracySwitch.class, async = false) })
	public PartitionedMonitoringData<PCMContextRecord> filterMonitoringData(
			PartitionedMonitoringData<IMonitoringRecord> monitoringData) {
		List<IMonitoringRecord> records = monitoringData.getAllData();

		// EVALUATION
		getBlackboard().getQuery().trackStartPipelineExecution();
		getBlackboard().getQuery().trackRecordCount(records.size());

		super.trackStart();
		// original logic
		log.info("Reached entry (size = " + records.size() + ").");
		List<PCMContextRecord> result = records.stream().filter(r -> r instanceof PCMContextRecord)
				.map(PCMContextRecord.class::cast).collect(Collectors.toList());

		super.trackEnd();

		return new PCMPartionedMonitoringData(result, monitoringData.getValidationSplit());
	}

}
