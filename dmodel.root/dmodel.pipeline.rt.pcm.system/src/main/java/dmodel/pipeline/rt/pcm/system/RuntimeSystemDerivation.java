package dmodel.pipeline.rt.pcm.system;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;

public class RuntimeSystemDerivation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	private static final Logger LOG = LoggerFactory.getLogger(RuntimeSystemDerivation.class);

	@InputPorts({ @InputPort(PortIDs.T_PCM_SYSTEM) })
	public void deriveSystemData(List<Tree<ServiceCallRecord>> entryCalls) {
		LOG.info("Deriving system refinements at runtime.");
		if (getBlackboard().getServiceCallGraph() != null) {
			// TODO
		} else {
			LOG.warn("System derivation at runtime needs a service call graph.");
			LOG.warn("Skipping system refinement.");
		}
	}

}
