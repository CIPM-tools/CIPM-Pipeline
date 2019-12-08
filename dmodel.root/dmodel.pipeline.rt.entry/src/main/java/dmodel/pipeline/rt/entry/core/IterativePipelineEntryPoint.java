package dmodel.pipeline.rt.entry.core;

import java.util.List;
import java.util.stream.Collectors;

import dmodel.pipeline.monitoring.records.ServiceContextRecord;
import dmodel.pipeline.rt.pcm.validation.PrePipelineValidationTask;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.EntryInputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.annotation.PipelineEntryPoint;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.router.AccuracySwitch;
import dmodel.pipeline.shared.pipeline.PortIDs;
import kieker.common.record.IMonitoringRecord;
import lombok.extern.java.Log;

@PipelineEntryPoint
@Log
public class IterativePipelineEntryPoint extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@EntryInputPort
	@OutputPorts({ @OutputPort(id = PortIDs.T_VAL_PRE, to = PrePipelineValidationTask.class, async = false),
			@OutputPort(id = PortIDs.T_RAW_ROUTER, to = AccuracySwitch.class, async = false) })
	public List<ServiceContextRecord> filterMonitoringData(List<IMonitoringRecord> records) {
		log.info("Reached entry (size = " + records.size() + ").");
		return records.stream().filter(r -> r instanceof ServiceContextRecord).map(ServiceContextRecord.class::cast)
				.collect(Collectors.toList());
	}

}
