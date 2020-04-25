package dmodel.runtime.pipelinepcm.usagemodel.data;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.BranchTransition;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import com.google.common.collect.Lists;

import dmodel.runtime.pipelinepcm.usagemodel.util.UsageServiceUtil;
import lombok.Data;

@Data
public class UsageBranchTransition implements IPCMAnalogue<BranchTransition> {
	private List<IAbstractUsageDescriptor> childs;
	private double probability;

	public UsageBranchTransition() {
		this.childs = Lists.newArrayList();
	}

	public boolean matches(UsageBranchTransition other) {
		if (childs.size() == other.childs.size()) {
			for (int i = 0; i < childs.size(); i++) {
				if (!childs.get(i).matches(other.childs.get(i))) {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public BranchTransition toPCM() {
		BranchTransition trans = UsagemodelFactory.eINSTANCE.createBranchTransition();
		trans.setBranchProbability(probability);
		trans.setBranchedBehaviour_BranchTransition(UsageServiceUtil.createBehaviour(childs));

		return trans;
	}
}
