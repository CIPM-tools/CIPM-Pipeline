package dmodel.pipeline.rt.entry.contracts.core;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.entry.contracts.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.entry.contracts.annotation.OutputPort;
import dmodel.pipeline.rt.entry.contracts.annotation.PipelineEntryPoint;
import dmodel.pipeline.rt.entry.contracts.blackboard.RuntimePipelineBlackboard;
import kieker.common.record.IMonitoringRecord;

@PipelineEntryPoint
public class IterativePipelineEntryPoint extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {
	private static final Logger LOG = LoggerFactory.getLogger(IterativePipelineEntryPoint.class);

	@OutputPort(to = ServiceCallEntryPoint.class, async = true)
	public List<ServiceCallRecord> filterServiceCalls(List<IMonitoringRecord> records) {
		LOG.info("Reached entry (size = " + records.size() + ").");
		return records.stream().filter(r -> r instanceof ServiceCallRecord).map(ServiceCallRecord.class::cast)
				.collect(Collectors.toList());
	}

}
