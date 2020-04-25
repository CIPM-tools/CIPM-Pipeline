package dmodel.runtime.pipeline.pcm.repository.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

import dmodel.base.shared.pcm.distribution.IntDistribution;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

// TODO it is unclean to return null if its an too high result
// this should be modeled different
public class ResourceDemandModel implements IResourceDemandModel {
	private static final long NANO_TO_MS = 1000L * 1000L;

	private List<ResourceDemandTriple> data;

	public ResourceDemandModel() {
		this.data = new ArrayList<>();
	}

	@Override
	public void put(ResourceDemandTriple trip) {
		this.data.add(trip);
	}

	@Override
	public Pair<PCMRandomVariable, Double[]> deriveStochasticExpression(float thres) {
		LinearRegression regression = new LinearRegression();
		// get attributes
		List<String> attributes = getDependentParameters(thres);

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

		for (ResourceDemandTriple triple : data) {
			double durationInMS = triple.getTime() / NANO_TO_MS;
			double[] values = new double[dataset.numAttributes()];
			for (Entry<String, Object> parameter : triple.getParameters().getParameters().entrySet()) {
				if (attributeIndexMapping.containsKey(parameter.getKey())) {
					int index = attributeIndexMapping.get(parameter.getKey());
					double value = resolveParameterValue(parameter.getValue());
					values[index] = value;
				}
			}

			values[dataset.numAttributes() - 1] = durationInMS;
			DenseInstance instance = new DenseInstance(1, values);
			dataset.add(instance);
		}

		// get coefficients
		try {
			regression.buildClassifier(dataset);
			double[] coeff = regression.coefficients();
			IntDistribution constants = buildConstantDistribution(coeff, dataset);

			PCMRandomVariable var = CoreFactory.eINSTANCE.createPCMRandomVariable();
			var.setSpecification(getResourceDemandStochasticExpression(coeff, constants, indexAttributeMapping));
			if (var.getSpecification() == null) {
				return null;
			} else {
				return Pair.of(var, Arrays.stream(coeff).boxed().toArray(Double[]::new));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private IntDistribution buildConstantDistribution(double[] coeff, Instances dataset) {
		IntDistribution distr = new IntDistribution();

		for (Instance instance : dataset) {
			double sum = 0;
			for (int k = 1; k < coeff.length - 1; k++) {
				sum += coeff[k] * instance.value(k - 1);
			}
			double constValue = instance.value(dataset.numAttributes() - 1) - sum;
			distr.push((int) Math.round(constValue));

			if (constValue >= 1000 * 60 * 60) {
				return null;
			}
		}

		return distr;
	}

	@Override
	public List<String> getDependentParameters(float thres) {
		Map<String, List<Pair<Double, Double>>> parameterMap = new HashMap<>();
		for (ResourceDemandTriple triple : data) {
			for (Entry<String, Object> parameter : triple.getParameters().getParameters().entrySet()) {
				double parameterValue = resolveParameterValue(parameter.getValue());

				if (!Double.isNaN(parameterValue)) {
					// add a pair
					if (!parameterMap.containsKey(parameter.getKey())) {
						parameterMap.put(parameter.getKey(), new ArrayList<>());
					}
					parameterMap.get(parameter.getKey()).add(Pair.of(parameterValue, triple.getTime()));
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

	// TODO to be more accurate we could use a double distribution
	private String getResourceDemandStochasticExpression(double[] coefficients, IntDistribution constant,
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
		result.add(constant.toStochasticExpression().getSpecification());
		StringBuilder strBuilder = new StringBuilder().append(result.toString());
		for (int i = 0; i < braces; i++) {
			strBuilder.append(")");
		}
		return strBuilder.toString();
	}

}
