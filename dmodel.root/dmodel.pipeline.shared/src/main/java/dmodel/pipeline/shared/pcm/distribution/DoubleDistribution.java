package dmodel.pipeline.shared.pcm.distribution;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

public class DoubleDistribution {

	private Map<Double, Long> occs;
	private int precision;

	public DoubleDistribution(int precision) {
		this.occs = new TreeMap<>();
		this.precision = precision;
	}

	public void put(double d) {
		double r = roundTo(d, precision);
		if (!occs.containsKey(r)) {
			occs.put(r, 1L);
		} else {
			occs.put(r, occs.get(r) + 1);
		}
	}

	public void multiply(double factor) {
		Map<Double, Long> copy = new HashMap<>();
		occs.entrySet().forEach(et -> copy.put(roundTo(et.getKey() * factor, precision), et.getValue()));
		this.occs = copy;
	}

	public void divide(double factor) {
		this.multiply(1 / factor);
	}

	public PCMRandomVariable toStoex() {
		if (occs.size() == 1) {
			return buildDoubleLiteral(occs.entrySet().stream().findFirst().get().getKey());
		} else {
			double sum = occs.entrySet().stream().mapToLong(l -> l.getValue()).sum();
			StringBuilder builder = new StringBuilder();
			builder.append("DoublePMF[");
			for (Entry<Double, Long> entry : occs.entrySet()) {
				double prob = (double) entry.getValue() / sum;
				builder.append("(");
				builder.append(String.valueOf(entry.getKey()));
				builder.append(";");
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

	private PCMRandomVariable buildDoubleLiteral(double value) {
		return buildPCMVar(String.valueOf(value));
	}

	private PCMRandomVariable buildPCMVar(String value) {
		PCMRandomVariable ret = CoreFactory.eINSTANCE.createPCMRandomVariable();
		ret.setSpecification(value);
		return ret;
	}

	private double roundTo(double d, int precision) {
		double factor = Math.pow(10, precision);
		return Math.round(d * factor) / factor;
	}

}