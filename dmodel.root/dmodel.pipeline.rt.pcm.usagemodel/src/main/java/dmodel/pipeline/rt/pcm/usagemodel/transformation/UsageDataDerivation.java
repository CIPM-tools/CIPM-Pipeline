package dmodel.pipeline.rt.pcm.usagemodel.transformation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dmodel.pipeline.dt.mmmodel.UsageData;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.IUsageDataExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.TreeBranchExtractor;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;

public class UsageDataDerivation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {
	private static final Logger LOG = LoggerFactory.getLogger(UsageDataDerivation.class);

	private IUsageDataExtractor treeExtractor;

	public UsageDataDerivation() {
		this.treeExtractor = new TreeBranchExtractor();
	}

	@InputPorts(@InputPort(PortIDs.T_PCM_USAGE))
	public void deriveUsageData(List<Tree<ServiceCallRecord>> callTrees) {
		// 1. derive usage data
		UsageData data = this.treeExtractor.extract(callTrees);

		LOG.info("Extracted " + data.getGroups().size() + " usage scenario(s).");

		// 2. set it
		getBlackboard().getMeasurementModel().setUsageData(data);
	}

}
