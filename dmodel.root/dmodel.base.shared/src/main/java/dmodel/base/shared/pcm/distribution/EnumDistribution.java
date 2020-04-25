package dmodel.base.shared.pcm.distribution;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

public class EnumDistribution {

	private Map<String, Long> distribution;

	public EnumDistribution() {
		this.distribution = new HashMap<>();
	}

	public void push(String value, long val) {
		if (!distribution.containsKey(value)) {
			distribution.put(value, val);
		} else {
			distribution.put(value, distribution.get(value) + val);
		}
	}

	public void push(String value) {
		this.push(value, 1L);
	}

	public void push(EnumDistribution other) {
		other.distribution.entrySet().forEach(v -> this.push(v.getKey(), v.getValue()));
	}

	public PCMRandomVariable toStoex() {
		if (distribution.size() == 1) {
			return buildStringLiteral(distribution.entrySet().stream().findFirst().get().getKey());
		} else {
			long sum = distribution.entrySet().stream().mapToLong(l -> l.getValue()).sum();
			StringBuilder builder = new StringBuilder();
			builder.append("EnumPMF[");
			for (Entry<String, Long> entry : distribution.entrySet()) {
				double prob = (double) entry.getValue() / (double) sum;
				builder.append("(\"");
				builder.append(String.valueOf(entry.getKey()));
				builder.append("\";");
				builder.append(String.valueOf(roundDouble(prob, 10)));
				builder.append(")");
			}
			builder.append("]");
			return buildPCMVar(builder.toString());
		}
	}

	private double roundDouble(double val, int chars) {
		double factor = Math.pow(10, chars);
		return Math.round(val * factor) / factor;
	}

	private PCMRandomVariable buildStringLiteral(String key) {
		return buildPCMVar("\"" + key + "\"");
	}

	private PCMRandomVariable buildPCMVar(String value) {
		PCMRandomVariable ret = CoreFactory.eINSTANCE.createPCMRandomVariable();
		ret.setSpecification(value);
		return ret;
	}

}