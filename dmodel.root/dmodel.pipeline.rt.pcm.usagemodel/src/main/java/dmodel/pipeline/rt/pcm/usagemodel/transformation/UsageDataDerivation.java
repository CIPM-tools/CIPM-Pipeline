package dmodel.pipeline.rt.pcm.usagemodel.transformation;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.IUsageDataExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.TreeBranchExtractor;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;
import lombok.extern.java.Log;

@Log
public class UsageDataDerivation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {
	private IUsageDataExtractor treeExtractor;

	public UsageDataDerivation() {
		this.treeExtractor = new TreeBranchExtractor();
	}

	@InputPorts(@InputPort(PortIDs.T_PCM_USAGE))
	public void deriveUsageData(List<Tree<ServiceCallRecord>> callTrees) {
		// 1. derive usage data
		List<UsageScenario> data = this.treeExtractor.extract(callTrees,
				getBlackboard().getArchitectureModel().getRepository(),
				getBlackboard().getArchitectureModel().getSystem());

		log.info("Extracted " + data.size() + " usage scenario(s).");

		// 2. deduct
		getBlackboard().getArchitectureModel().getUsageModel().getUsageScenario_UsageModel().clear();
		getBlackboard().getArchitectureModel().getUsageModel().getUsageScenario_UsageModel().addAll(data);
	}

}
