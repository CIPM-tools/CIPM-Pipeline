package dmodel.pipeline.rt.validation.data.metric.impl;

import org.apache.commons.math3.ml.distance.EarthMoversDistance;
import org.pcm.headless.shared.data.results.MeasuringPointType;

import dmodel.pipeline.rt.validation.data.ValidationPoint;
import dmodel.pipeline.rt.validation.data.metric.IValidationMetric;
import dmodel.pipeline.rt.validation.data.metric.ValidationMetricType;
import dmodel.pipeline.rt.validation.data.metric.value.DoubleMetricValue;

public class ServiceCallWassersteinDistanceMetric implements IValidationMetric<DoubleMetricValue> {

	@Override
	public boolean isTarget(ValidationPoint point) {
		if (point.getMeasuringPoint().getType() == MeasuringPointType.ASSEMBLY_OPERATION
				|| point.getMeasuringPoint().getType() == MeasuringPointType.ENTRY_LEVEL_CALL) {
			return true;
		}
		return false;
	}

	@Override
	public DoubleMetricValue calculate(ValidationPoint point) {
		if (point.getAnalysisDistribution() != null && point.getMonitoringDistribution() != null) {
			if (point.getAnalysisDistribution().getYValues().size() >= 2
					&& point.getMonitoringDistribution().getYValues().size() >= 2) {
				return new DoubleMetricValue(new EarthMoversDistance().compute(point.getAnalysisDistribution().yAxis(),
						point.getMonitoringDistribution().yAxis()), ValidationMetricType.WASSERSTEIN, false);
			}
		}
		return null;
	}

}