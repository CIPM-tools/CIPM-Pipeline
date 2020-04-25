package dmodel.runtime.pipeline.pcm.repository.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

public class IfDistribution {

	Map<IfCondition, PCMRandomVariable> inner;

	public IfDistribution() {
		this.inner = new HashMap<>();
	}

	public void put(IfCondition cond, PCMRandomVariable var) {
		this.inner.put(cond, var);
	}

	public PCMRandomVariable toStoex() {
		String all = "";

		String last = null;
		String lastSimple = null;

		int times = 0;
		for (Entry<IfCondition, PCMRandomVariable> e : inner.entrySet()) {
			if (last != null) {
				all += last;
			}

			last = "((" + e.getKey().getStoex().getSpecification() + ") ? ";
			last += "(" + e.getValue().getSpecification() + ") ";
			last += ": ";

			lastSimple = e.getValue().getSpecification();
			times++;
		}
		all += lastSimple;
		all += StringUtils.repeat(")", times - 1);

		PCMRandomVariable out = CoreFactory.eINSTANCE.createPCMRandomVariable();
		out.setSpecification(all);
		return out;
	}

}
