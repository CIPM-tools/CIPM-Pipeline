package dmodel.pipeline.rt.pcm.repository.regression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

import dmodel.pipeline.monitoring.util.ServiceParametersWrapper;
import dmodel.pipeline.rt.pcm.repository.data.IfCondition;
import dmodel.pipeline.rt.pcm.repository.data.IfDistribution;
import dmodel.pipeline.shared.pcm.distribution.DoubleDistribution;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class ParametricLinearRegression {

	private List<Pair<ServiceParametersWrapper, Double>> underlying;
	private float innerThres;
	private int prec;

	private static final int MAX_DEPTH = 5;

	public ParametricLinearRegression(List<Pair<ServiceParametersWrapper, Double>> data, int precision,
			float dependencyThreshold) {
		this.underlying = data;
		this.innerThres = dependencyThreshold;
		this.prec = precision;
	}

	public PCMRandomVariable deriveStoex(Map<String, String> parameterMapping) {
		// enum split
		Set<String> enumAttributes = getEnumParameters();

		if (enumAttributes.size() == 0) {
			return deriveInner(underlying);
		}

		Map<String, Set<String>> enumValueMapping = getEnumValues(enumAttributes);
		String firstKey = enumValueMapping.keySet().stream().findFirst().get();

		IfDistribution distr = new IfDistribution();

		// iterate
		for (String enumVal : enumValueMapping.get(firstKey)) {
			// TODO support combinations of enums
			IfCondition cond = new IfCondition(firstKey + "==\"" + enumVal + "\"");

			List<Pair<ServiceParametersWrapper, Double>> inner = underlying.stream().filter(f -> {
				return f.getKey().getParameters().entrySet().stream().anyMatch(e -> {
					return e.getKey().equals(firstKey) && e.getValue() instanceof String
							&& ((String) e.getValue()).equals(enumVal);
				});
			}).collect(Collectors.toList());

			distr.put(cond, this.deriveInner(inner));
		}

		return distr.toStoex();
	}

	private PCMRandomVariable deriveInner(List<Pair<ServiceParametersWrapper, Double>> inner) {
		LinearRegression regression = new LinearRegression();

		// get attributes
		List<String> attributes = getDependentParameters(this.innerThres, inner);

		ArrayList<Attribute> wekaAttributes = new ArrayList<>();
		Map<String, Integer> attributeIndexMapping = new HashMap<>();
		Map<Integer, String> indexAttributeMapping = new HashMap<>();
		int k = 0;
		for (String stringAttribute : attributes) {
			wekaAttributes.add(new Attribute(stringAttribute));
			indexAttributeMapping.put(k, stringAttribute);
			attributeIndexMapping.put(stringAttribute, k++);
		}
		wekaAttributes.add(new Attribute("class"));

		Instances dataset = new Instances("dataset", wekaAttributes, 0);
		dataset.setClassIndex(dataset.numAttributes() - 1);

		for (Pair<ServiceParametersWrapper, Double> tuple : inner) {
			double demand = tuple.getRight();
			double[] values = new double[dataset.numAttributes()];
			for (Entry<String, Object> parameter : tuple.getLeft().getParameters().entrySet()) {
				if (attributeIndexMapping.containsKey(parameter.getKey())) {
					int index = attributeIndexMapping.get(parameter.getKey());
					double value = resolveParameterValue(parameter.getValue());
					values[index] = value;
					// max ^ 5
					for (int i = 2; i <= MAX_DEPTH; i++) {
						String attrName = parameter.getKey() + "^" + String.valueOf(i);
						int indexInner = attributeIndexMapping.get(attrName);
						double valueInner = Math.pow(value, i);
						values[indexInner] = valueInner;
					}
				}
			}

			values[dataset.numAttributes() - 1] = demand;
			DenseInstance instance = new DenseInstance(1, values);
			dataset.add(instance);
		}

		// get coefficients
		try {
			regression.buildClassifier(dataset);
			double[] coeff = regression.coefficients();
			DoubleDistribution constants = buildConstantDistribution(coeff, dataset);

			PCMRandomVariable var = CoreFactory.eINSTANCE.createPCMRandomVariable();
			var.setSpecification(getResourceDemandStochasticExpression(coeff, constants, indexAttributeMapping));
			if (var.getSpecification() == null) {
				return null;
			} else {
				return var;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Map<String, Set<String>> getEnumValues(Set<String> enumAttributes) {
		Map<String, Set<String>> ret = new HashMap<>();

		this.underlying.stream().map(t -> t.getKey()).forEach(p -> {
			for (String str : enumAttributes) {
				if (p.getParameters().containsKey(str)) {
					String v = (String) p.getParameters().get(str);
					if (ret.containsKey(str)) {
						ret.get(str).add(v);
					} else {
						ret.put(str, new HashSet<>());
						ret.get(str).add(v);
					}
				}
			}
		});

		return ret;
	}

	private Set<String> getEnumParameters() {
		return this.underlying.stream().map(t -> t.getKey())
				.map(s -> s.getParameters().entrySet().stream().filter(f -> f.getValue() instanceof String)
						.map(t -> t.getKey()).collect(Collectors.toSet()))
				.flatMap(Set::stream).collect(Collectors.toSet());
	}

	private DoubleDistribution buildConstantDistribution(double[] coeff, Instances dataset) {
		DoubleDistribution distr = new DoubleDistribution(prec);

		for (Instance instance : dataset) {
			double sum = 0;
			for (int k = 0; k < coeff.length - 1; k++) {
				sum += coeff[k] * instance.value(k);
			}

			double constValue = instance.value(dataset.numAttributes() - 1) - sum;
			distr.put(constValue);

			if (constValue >= 1000 * 60 * 60) {
				return null;
			}
		}

		return distr;
	}

	private List<String> getDependentParameters(float thres, List<Pair<ServiceParametersWrapper, Double>> inner) {
		Map<String, List<Pair<Double, Double>>> parameterMap = new HashMap<>();
		for (Pair<ServiceParametersWrapper, Double> tuple : inner) {
			for (Entry<String, Object> parameter : tuple.getLeft().getParameters().entrySet()) {
				double parameterValue = resolveParameterValue(parameter.getValue());

				if (!Double.isNaN(parameterValue)) {
					// add a pair
					if (!parameterMap.containsKey(parameter.getKey())) {
						parameterMap.put(parameter.getKey(), new ArrayList<>());
					}
					parameterMap.get(parameter.getKey()).add(Pair.of(parameterValue, tuple.getRight()));

					// max ^ 5
					for (int i = 2; i <= MAX_DEPTH; i++) {
						String attrName = parameter.getKey() + "^" + String.valueOf(i);
						if (!parameterMap.containsKey(attrName)) {
							parameterMap.put(attrName, new ArrayList<>());
						}
						parameterMap.get(attrName).add(Pair.of(Math.pow(parameterValue, (double) i), tuple.getRight()));
					}
				}
			}
		}

		SpearmansCorrelation spCorr = new SpearmansCorrelation();
		List<String> retList = new ArrayList<>();
		for (Entry<String, List<Pair<Double, Double>>> entry : parameterMap.entrySet()) {
			double[] arr1 = entry.getValue().stream().mapToDouble(d -> d.getLeft()).toArray();
			double[] arr2 = entry.getValue().stream().mapToDouble(d -> d.getRight()).toArray();

			double corr = spCorr.correlation(arr1, arr2);
			if (!Double.isNaN(corr) && Math.abs(corr) >= thres) {
				// it is NaN when one variable is constant
				retList.add(entry.getKey());
			}
		}

		return retList;
	}

	private double resolveParameterValue(Object val) {
		double parameterValue = Double.NaN;
		if (val instanceof Integer) {
			parameterValue = (int) val;
		} else if (val instanceof Double) {
			parameterValue = (double) val;
		} else if (val instanceof Long) {
			parameterValue = (long) val;
		}

		return parameterValue;
	}

	private String getResourceDemandStochasticExpression(double[] coefficients, DoubleDistribution constant,
			Map<Integer, String> parameterMapping) {
		if (constant == null) {
			return null;
		}

		StringJoiner result = new StringJoiner(" + (");
		int braces = 0;
		for (int i = 0; i < coefficients.length - 2; i++) {
			if (coefficients[i] == 0.0) {
				continue;
			}
			StringBuilder coefficientPart = new StringBuilder();
			String paramStoEx = parameterMapping.get(i);
			coefficientPart.append(coefficients[i]).append(" * ").append(paramStoEx);
			result.add(coefficientPart.toString());
			braces++;
		}
		result.add(constant.toStoex().getSpecification());
		StringBuilder strBuilder = new StringBuilder().append(result.toString());
		for (int i = 0; i < braces; i++) {
			strBuilder.append(")");
		}
		return strBuilder.toString();
	}

}
