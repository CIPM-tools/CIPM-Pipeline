package dmodel.runtime.pipeline.pcm.repository.loop.impl;

import java.util.StringJoiner;

import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.designtime.monitoring.util.ServiceParametersWrapper;
import dmodel.runtime.pipeline.pcm.repository.data.WekaDataSet;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;

public class WekaLoopModel implements LoopModel {

	private final LinearRegression classifier;

	private final WekaDataSet<Long> dataset;

	public WekaLoopModel(final WekaDataSet<Long> dataset) throws Exception {
		this.dataset = dataset;

		this.classifier = new LinearRegression();
		this.classifier.buildClassifier(this.dataset.getDataSet());
	}

	public LinearRegression getClassifier() {
		return classifier;
	}

	public Instances getDataSet() {
		return dataset.getDataSet();
	}

	@Override
	public double predictIterations(final ServiceCallRecord serviceCall) {
		Instance parametersInstance = this.dataset
				.buildTestInstance(ServiceParametersWrapper.buildFromJson(serviceCall.getParameters()));
		try {
			return this.classifier.classifyInstance(parametersInstance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getIterationsStochasticExpression() {
		StringJoiner result = new StringJoiner(" + (");
		double[] coefficients = this.classifier.coefficients();
		int braces = 0;
		for (int i = 0; i < coefficients.length - 2; i++) {
			int coefficient = round(coefficients[i]);
			if (coefficient == 0) {
				continue;
			}
			StringBuilder coefficientPart = new StringBuilder();
			String paramStoEx = this.dataset.getStochasticExpressionForIndex(i);
			coefficientPart.append(coefficient).append(" * ").append(paramStoEx);
			result.add(coefficientPart.toString());
			braces++;
		}
		result.add(String.valueOf(round(coefficients[coefficients.length - 1])));
		StringBuilder strBuilder = new StringBuilder().append(result.toString());
		for (int i = 0; i < braces; i++) {
			strBuilder.append(")");
		}
		return strBuilder.toString();
	}

	private static int round(final double value) {
		return (int) Math.round(value);
	}
}