package dmodel.pipeline.rt.validation.data.metric.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.distance.EarthMoversDistance;
import org.springframework.stereotype.Service;

import dmodel.pipeline.rt.validation.data.ValidationPoint;
import dmodel.pipeline.rt.validation.data.metric.ValidationMetricType;
import dmodel.pipeline.rt.validation.data.metric.value.DoubleMetricValue;

@Service
public class ServiceCallWassersteinDistanceMetric extends ServiceCallMetric {

	@Override
	public DoubleMetricValue calculate(ValidationPoint point) {
		if (point.getAnalysisDistribution() != null && point.getMonitoringDistribution() != null) {
			if (point.getAnalysisDistribution().getYValues().size() >= 2
					&& point.getMonitoringDistribution().getYValues().size() >= 2) {

				int sizeMonitoring = point.getMonitoringDistribution().getYValues().size();
				int sizeAnalysis = point.getAnalysisDistribution().getYValues().size();

				double[] valuesMonitoring;
				double[] valuesAnalysis;
				if (sizeMonitoring < sizeAnalysis) {
					// we need to adjust them
					valuesMonitoring = point.getMonitoringDistribution().yAxis();
					valuesAnalysis = randomlySelect(point.getAnalysisDistribution().yAxis(), sizeMonitoring);
				} else if (sizeMonitoring > sizeAnalysis) {
					// we need to adjust the
					valuesAnalysis = point.getAnalysisDistribution().yAxis();
					valuesMonitoring = randomlySelect(point.getMonitoringDistribution().yAxis(), sizeAnalysis);
				} else {
					valuesAnalysis = point.getAnalysisDistribution().yAxis();
					valuesMonitoring = point.getMonitoringDistribution().yAxis();
				}

				return new DoubleMetricValue(new EarthMoversDistance().compute(valuesAnalysis, valuesMonitoring),
						ValidationMetricType.WASSERSTEIN, false);
			}
		}
		return null;
	}

	private double[] randomlySelect(double[] arr, int size) {
		Random rand = new Random();
		List<Double> copyList = Arrays.stream(arr).boxed().collect(Collectors.toList());
		double[] output = new double[size];
		for (int k = 0; k < size; k++) {
			output[k] = copyList.remove(rand.nextInt(copyList.size()));
		}
		return output;
	}

}
