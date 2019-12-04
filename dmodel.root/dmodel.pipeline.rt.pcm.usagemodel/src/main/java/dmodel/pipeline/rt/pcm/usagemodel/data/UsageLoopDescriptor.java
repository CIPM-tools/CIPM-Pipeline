package dmodel.pipeline.rt.pcm.usagemodel.data;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Loop;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import com.google.common.collect.Lists;

import dmodel.pipeline.rt.pcm.usagemodel.util.UsageServiceUtil;
import dmodel.pipeline.shared.pcm.distribution.IntDistribution;
import lombok.Data;

@Data
public class UsageLoopDescriptor implements IAbstractUsageDescriptor {

	private List<Integer> iterations;
	private List<IAbstractUsageDescriptor> childs;

	public UsageLoopDescriptor() {
		this.iterations = Lists.newArrayList();
		this.childs = Lists.newArrayList();
	}

	@Override
	public boolean matches(IAbstractUsageDescriptor other) {
		if (other instanceof UsageLoopDescriptor) {
			UsageLoopDescriptor uldOther = (UsageLoopDescriptor) other;
			if (uldOther.childs.size() == childs.size()) {
				for (var i = 0; i < childs.size(); i++) {
					if (!childs.get(i).matches(uldOther.childs.get(i))) {
						return false;
					}
				}
				return true;
			}
		}

		return false;
	}

	@Override
	public void merge(IAbstractUsageDescriptor other) {
		if (other instanceof UsageLoopDescriptor) {
			UsageLoopDescriptor uldOther = (UsageLoopDescriptor) other;
			this.iterations.addAll(uldOther.getIterations());

			for (var i = 0; i < childs.size(); i++) {
				childs.get(i).merge(uldOther.childs.get(i));
			}
		}
	}

	@Override
	public AbstractUserAction toPCM() {
		Loop ret = UsagemodelFactory.eINSTANCE.createLoop();

		ret.setBodyBehaviour_Loop(UsageServiceUtil.createBehaviour(childs));

		IntDistribution distr = new IntDistribution();
		distr.pushAll(iterations);

		ret.setLoopIteration_Loop(distr.toStochasticExpression());

		return ret;
	}

}
