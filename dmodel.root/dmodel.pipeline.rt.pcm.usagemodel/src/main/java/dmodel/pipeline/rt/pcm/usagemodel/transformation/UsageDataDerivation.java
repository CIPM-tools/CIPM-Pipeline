package dmodel.pipeline.rt.pcm.usagemodel.transformation;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.IUsageDataExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.TreeBranchExtractor;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.structure.Tree;
import lombok.extern.java.Log;

@Log
public class UsageDataDerivation {
	private IUsageDataExtractor treeExtractor;

	public UsageDataDerivation() {
		this.treeExtractor = new TreeBranchExtractor();
	}

	public void deriveUsageData(List<Tree<ServiceCallRecord>> callTrees, InMemoryPCM pcm) {
		// 1. derive usage data
		List<UsageScenario> data = this.treeExtractor.extract(callTrees, pcm.getRepository(), pcm.getSystem());

		log.info("Extracted " + data.size() + " usage scenario(s).");

		// 2. deduct
		if (pcm.getUsageModel() == null) {
			pcm.setUsageModel(UsagemodelFactory.eINSTANCE.createUsageModel());
		}
		pcm.getUsageModel().getUsageScenario_UsageModel().clear();
		pcm.getUsageModel().getUsageScenario_UsageModel().addAll(data);
	}

}
