package dmodel.pipeline.rt.pcm.allocation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.system.RuntimeSystemDerivation;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;

public class AllocationDerivation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {
	private static final Logger LOG = LoggerFactory.getLogger(AllocationDerivation.class);

	@InputPorts({ @InputPort(PortIDs.T_SC_PCM_ALLOCATION), @InputPort(PortIDs.T_RESENV_PCM_ALLOCATION) })
	@OutputPorts(@OutputPort(to = RuntimeSystemDerivation.class, async = false, id = PortIDs.T_ALLOCATION_PCM_SYSTEM))
	public void deriveAllocationData(List<Tree<ServiceCallRecord>> entryCalls) {
		LOG.info("Deriving allocations.");
	}

}
