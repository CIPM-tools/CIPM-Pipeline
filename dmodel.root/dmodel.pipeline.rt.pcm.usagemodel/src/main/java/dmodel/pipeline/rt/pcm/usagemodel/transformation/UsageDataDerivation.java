package dmodel.pipeline.rt.pcm.usagemodel.transformation;

import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.springframework.stereotype.Service;

import dmodel.pipeline.core.facade.IPCMQueryFacade;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.IUsageDataExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.tree.TreeBranchExtractor;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.shared.structure.Tree;
import lombok.extern.java.Log;

@Log
@Service
public class UsageDataDerivation {
	private IUsageDataExtractor treeExtractor;

	public UsageDataDerivation() {
		this.treeExtractor = new TreeBranchExtractor();
	}

	public List<UsageScenario> deriveUsageData(List<Tree<ServiceCallRecord>> callTrees, IPCMQueryFacade pcm,
			ValidationData validation) {
		// 1. derive usage data
		List<UsageScenario> data = this.treeExtractor.extract(callTrees, pcm.getRepository(), pcm.getSystem());
		data = data.stream()
				.filter(dt -> dt.getScenarioBehaviour_UsageScenario().getActions_ScenarioBehaviour().size() > 2)
				.collect(Collectors.toList());

		log.info("Extracted " + data.size() + " usage scenario(s).");

		// 2. deduct
		return data;
	}

}
