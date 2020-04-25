package dmodel.runtime.pipelinepcm.usagemodel.data;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Branch;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class UsageBranchDescriptor implements IAbstractUsageDescriptor {
	private List<UsageBranchTransition> childs;

	public UsageBranchDescriptor() {
		this.childs = Lists.newArrayList();
	}

	@Override
	public boolean matches(IAbstractUsageDescriptor other) {
		if (other instanceof UsageBranchDescriptor) {
			UsageBranchDescriptor ubdOther = (UsageBranchDescriptor) other;
			if (childs.size() == ubdOther.childs.size()) {
				for (int i = 0; i < childs.size(); i++) {
					if (!childs.get(i).matches(ubdOther.childs.get(i))) {
						return false;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void merge(IAbstractUsageDescriptor other) {
		throw new UnsupportedOperationException("Merging of branches is not allowed.");
	}

	@Override
	public AbstractUserAction toPCM() {
		Branch ret = UsagemodelFactory.eINSTANCE.createBranch();

		childs.forEach(ch -> {
			ret.getBranchTransitions_Branch().add(ch.toPCM());
		});

		return ret;
	}

}
