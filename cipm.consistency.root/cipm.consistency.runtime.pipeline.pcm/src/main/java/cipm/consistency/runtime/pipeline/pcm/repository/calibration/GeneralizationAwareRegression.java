package cipm.consistency.runtime.pipeline.pcm.repository.calibration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cipm.consistency.base.core.config.ModelCalibrationConfiguration;
import cipm.consistency.base.shared.pcm.util.PCMUtils;
import cipm.consistency.runtime.pipeline.pcm.repository.IParametricRegression;
import cipm.consistency.runtime.pipeline.pcm.repository.IRegressionModelNoiseGenerator;
import cipm.consistency.runtime.pipeline.pcm.repository.calibration.ExecutionTimesExtractor.RegressionDataset;
import cipm.consistency.runtime.pipeline.pcm.repository.noise.DeviationDistributionNoise;
import cipm.consistency.runtime.pipeline.pcm.repository.noise.NormalDistributionNoise;
import lombok.AllArgsConstructor;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.regression.ElasticNet;
import smile.regression.LinearModel;

public class GeneralizationAwareRegression implements IParametricRegression {
	private static final double EPS = 1E-6;

	private List<PCMSupportedMathFunction> supportedFunctions;

	public GeneralizationAwareRegression() {
	}

	@Override
	public PCMRandomVariable performRegression(RegressionDataset dataset, ModelCalibrationConfiguration config) {
		IRegressionModelNoiseGenerator noiseGenerator;
		if (config.getNoiseStrategy() == 0) {
			noiseGenerator = new NormalDistributionNoise(config.getNoiseOutlierPercentile());
		} else if (config.getNoiseStrategy() == 1) {
			noiseGenerator = new DeviationDistributionNoise(config.getNoiseOutlierPercentile());
		} else {
			noiseGenerator = new DummyNoiseGenerator();
		}

		this.prepareSupportedFunctions(config);

		// parameter mapping
		int numAttributes = 0;
		Map<String, Integer> numAttributeMapping = Maps.newHashMap();
		for (Pair<Map<String, Double>, Double> row : dataset.getRecords()) {
			for (Entry<String, Double> e : row.getLeft().entrySet()) {
				if (!numAttributeMapping.containsKey(e.getKey())) {
					numAttributeMapping.put(e.getKey(), numAttributes++);
				}
			}
		}

		// generate data (generate dataset)
		double[][] data = generateDataset(dataset, numAttributeMapping);
		String[] labels = generateLabels(numAttributeMapping);

		// filter constant columns
		List<Integer> constantColumns = determineConstantColumns(data);
		data = cutColumns(data, constantColumns);
		labels = cutColumns(labels, constantColumns);

		if (data[0].length <= 1) {
			return constantRegression(data, noiseGenerator);
		}

		// do the first regression
		DataFrame df = DataFrame.of(data, labels);
		Formula formula = Formula.lhs("y");
		// LinearModel model = ElasticNet.fit(formula, df, 0.9, 0.0053);
		LinearModel model = ElasticNet.fit(formula, df, 0.1, 0.001);
		// LinearModel model = LASSO.fit(formula, df);

		// refit model
		boolean filterable = true;

		while (filterable) {
			// cut data
			double[] weights = model.coefficients();
			List<Integer> filterColumns = IntStream.range(0, weights.length).filter(i -> {
				return Math.abs(weights[i]) < config.getParameterSignificanceThreshold();
			}).boxed().collect(Collectors.toList());
			data = cutColumns(data, filterColumns);
			labels = cutColumns(labels, filterColumns);

			// rebuild model
			if (labels.length == 1) {
				// constant regression
				return constantRegression(data, noiseGenerator);
			}
			df = DataFrame.of(data, labels);
			formula = Formula.lhs("y");
			model = ElasticNet.fit(formula, df, 0.1, 0.001);
			// model = LASSO.fit(formula, df);

			// new ttests
			filterable = Arrays.stream(model.coefficients())
					.anyMatch(t -> Math.abs(t) < config.getParameterSignificanceThreshold());
		}

		// noise step
		double[] deviations = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			double[] xValueCopy = new double[data[i].length - 1];
			System.arraycopy(data[i], 0, xValueCopy, 0, data[i].length - 1);

			deviations[i] = data[i][data[i].length - 1] - model.predict(xValueCopy);
		}

		// generate noise
		PCMRandomVariable var = noiseGenerator.generateNoise(deviations, 80);

		// convert to stoex
		PCMRandomVariable linearModelStoex = generateLinearModelStoex(model, labels);
		// combine stoexs
		PCMRandomVariable linearModelAndNoiseStoex = CoreFactory.eINSTANCE.createPCMRandomVariable();
		linearModelAndNoiseStoex.setSpecification("(" + linearModelStoex.getSpecification() + " + " + model.intercept()
				+ ") + " + var.getSpecification());
		linearModelAndNoiseStoex.setSpecification("Max(0.0," + linearModelAndNoiseStoex.getSpecification() + ")");

		return linearModelAndNoiseStoex;
	}

	private PCMRandomVariable generateLinearModelStoex(LinearModel model, String[] labels) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < model.coefficients().length; i++) {
			if (i > 0) {
				builder.append(" + ");
			}
			builder.append("(");
			builder.append(String.valueOf(model.coefficients()[i]) + " * " + labels[i]);
		}
		for (int i = 0; i < model.coefficients().length; i++) {
			builder.append(")");
		}
		PCMRandomVariable var = CoreFactory.eINSTANCE.createPCMRandomVariable();
		var.setSpecification(builder.toString());
		return var;
	}

	private PCMRandomVariable constantRegression(double[][] data, IRegressionModelNoiseGenerator noiser) {
		// TODO maybe implement another strategy
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i][0];
		}
		double avg = sum / data.length;
		double[] deviations = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			deviations[i] = data[i][0] - avg;
		}
		PCMRandomVariable noise = noiser.generateNoise(deviations, 100);
		PCMRandomVariable predictor = CoreFactory.eINSTANCE.createPCMRandomVariable();
		predictor.setSpecification(String.valueOf(avg) + " + " + noise.getSpecification());
		predictor.setSpecification("Max(0.0," + predictor.getSpecification() + ")"); // -> always >= 0
		return predictor;
	}

	private double[][] generateDataset(RegressionDataset dataset, Map<String, Integer> parameterMapping) {
		double[][] result = new double[dataset.getRecords()
				.size()][parameterMapping.size() * (supportedFunctions.size() + 1) + 1];

		int c = 0;
		for (Pair<Map<String, Double>, Double> row : dataset.getRecords()) {
			int cc = c;
			for (Entry<String, Double> e : row.getLeft().entrySet()) {
				int id = parameterMapping.get(e.getKey()) * (supportedFunctions.size() + 1);
				result[cc][id] = e.getValue();
				for (int z = 0; z < supportedFunctions.size(); z++) {
					result[cc][id + z + 1] = supportedFunctions.get(z).calculate(e.getValue());
				}
			}
			result[cc][result[cc].length - 1] = row.getValue();
			c++;
		}

		return result;
	}

	private String[] generateLabels(Map<String, Integer> parameterMapping) {
		String[] labels = new String[parameterMapping.size() * (supportedFunctions.size() + 1) + 1];
		for (Entry<String, Integer> e : parameterMapping.entrySet()) {
			int id = parameterMapping.get(e.getKey()) * (supportedFunctions.size() + 1);
			labels[id] = e.getKey();
			for (int z = 0; z < supportedFunctions.size(); z++) {
				labels[id + z + 1] = supportedFunctions.get(z).transform(e.getKey());
			}
		}
		labels[labels.length - 1] = "y";
		return labels;
	}

	private String[] cutColumns(String[] labels, List<Integer> constantColumns) {
		String[] copy = new String[labels.length - constantColumns.size()];
		int ci = 0;
		for (int i = 0; i < labels.length; i++) {
			if (!constantColumns.contains(i)) {
				copy[ci++] = labels[i];
			}
		}
		return copy;
	}

	private double[][] cutColumns(double[][] data, List<Integer> constantColumns) {
		double[][] copy = new double[data.length][data[0].length - constantColumns.size()];
		for (int i = 0; i < data.length; i++) {
			int cj = 0;
			for (int j = 0; j < data[i].length; j++) {
				if (!constantColumns.contains(j)) {
					copy[i][cj++] = data[i][j];
				}
			}
		}
		return copy;
	}

	private List<Integer> determineConstantColumns(double[][] data) {
		List<Integer> result = Lists.newArrayList();
		if (data.length == 0)
			return result;

		for (int i = 0; i < data[0].length - 1; i++) {
			double ival = data[0][i];
			for (int j = 0; j < data.length; j++) {
				if (Math.abs(data[j][i] - ival) > EPS) {
					break;
				} else if (j == data.length - 1) {
					result.add(i);
				}
			}
		}

		return result;
	}

	private void prepareSupportedFunctions(ModelCalibrationConfiguration config) {
		supportedFunctions = Lists.newArrayList();
		int powDepth = config.getPowDepth();

		for (int i = 2; i <= powDepth; i++) {
			int k = i;
			supportedFunctions.add(new PCMSupportedMathFunction(d -> Math.pow(d, k), s -> s + "^" + k));
		}

		if (config.isSqrtFunction()) {
			supportedFunctions.add(new PCMSupportedMathFunction(d -> Math.sqrt(d), d -> "Sqrt(" + d + ")")); // sqrt(x)
		}

		if (config.isLnFunction()) {
			supportedFunctions.add(new PCMSupportedMathFunction(d -> Math.log(Math.max(d, 1)),
					d -> "Log(" + String.valueOf(Math.E) + ",Max(1.0," + d + " * 1.0))")); // ln(x)
		}

		if (config.isExponentialFunction()) {
			supportedFunctions.add(new PCMSupportedMathFunction(d -> Math.pow(Math.E, d),
					d -> "(" + String.valueOf(Math.E) + "^(" + d + "))"));
		}
	}

	@AllArgsConstructor
	private static class PCMSupportedMathFunction {
		private Function<Double, Double> javaFunction;
		private Function<String, String> pcmFunction;

		public double calculate(double input) {
			return javaFunction.apply(input).doubleValue();
		}

		public String transform(String input) {
			return pcmFunction.apply(input);
		}
	}

	private static class DummyNoiseGenerator implements IRegressionModelNoiseGenerator {
		@Override
		public PCMRandomVariable generateNoise(double[] deviations, int maxSize) {
			return PCMUtils.createRandomVariableFromString("0.0");
		}
	}

}
