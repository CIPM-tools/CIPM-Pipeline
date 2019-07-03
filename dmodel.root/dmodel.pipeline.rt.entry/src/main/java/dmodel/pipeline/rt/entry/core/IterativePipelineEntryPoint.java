package dmodel.pipeline.rt.entry.core;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.EntryInputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.annotation.PipelineEntryPoint;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import kieker.common.record.IMonitoringRecord;

@PipelineEntryPoint
public class IterativePipelineEntryPoint extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {
	private static final Logger LOG = LoggerFactory.getLogger(IterativePipelineEntryPoint.class);

	@EntryInputPort
	@OutputPorts(ports = { @OutputPort(id = "toentry", to = ServiceCallEntryPoint.class, async = true) })
	public List<ServiceCallRecord> filterServiceCalls(List<IMonitoringRecord> records) {
		LOG.info("Reached entry (size = " + records.size() + ").");
		return records.stream().filter(r -> r instanceof ServiceCallRecord).map(ServiceCallRecord.class::cast)
				.collect(Collectors.toList());
	}

}
