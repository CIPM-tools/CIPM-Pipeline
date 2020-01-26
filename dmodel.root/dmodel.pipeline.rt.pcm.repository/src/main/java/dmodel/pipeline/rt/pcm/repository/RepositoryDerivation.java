package dmodel.pipeline.rt.pcm.repository;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.math3.stat.StatUtils;
import org.pcm.headless.shared.data.results.MeasuringPointType;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import dmodel.pipeline.models.mapping.PalladioRuntimeMapping;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.rt.pcm.repository.branch.impl.BranchEstimationImpl;
import dmodel.pipeline.rt.pcm.repository.core.ResourceDemandEstimatorAlternative;
import dmodel.pipeline.rt.pcm.repository.loop.impl.LoopEstimationImpl;
import dmodel.pipeline.rt.pcm.repository.model.IResourceDemandEstimator;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.rt.validation.data.ValidationMetricValue;
import dmodel.pipeline.rt.validation.data.metric.ValidationMetricType;
import dmodel.pipeline.rt.validation.data.metric.value.DoubleMetricValue;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import lombok.extern.java.Log;

@Service
@Log
public class RepositoryDerivation {
	private static final double ADJUSTMENT_FACTOR = 0.1d;
	private static final double THRES_REL_DIST = 0.1d;

	private final LoopEstimationImpl loopEstimation;
	private final BranchEstimationImpl branchEstimation;

	private Map<String, Double> currentValidationAdjustment = Maps.newHashMap();
	private Map<String, Double> currentServiceAdjustment = Maps.newHashMap();

	public RepositoryDerivation() {
		this.loopEstimation = new LoopEstimationImpl();
		this.branchEstimation = new BranchEstimationImpl();
	}

	public void calibrateRepository(List<PCMContextRecord> data, InMemoryPCM pcm, PalladioRuntimeMapping mapping,
			ValidationData validation) {
		try {
			prepareAdjustment(validation);

			MonitoringDataSet monitoringDataSet = new MonitoringDataSet(data, mapping, pcm.getAllocationModel(),
					pcm.getRepository());

			IResourceDemandEstimator estimation = new ResourceDemandEstimatorAlternative(pcm);
			estimation.prepare(monitoringDataSet);
			estimation.derive(currentValidationAdjustment);

			log.info("Finished calibration of internal actions.");
			log.info("Finished repository calibration.");
		} catch (Exception e) {
			log.info("Calibration failed.");
			log.log(Level.INFO, "Calibrate Repository failed.", e);
		}

	}

	private void prepareAdjustment(ValidationData validation) {

		validation.getValidationPoints().forEach(point -> {
			MeasuringPointType type = point.getMeasuringPoint().getType();
			if (type == MeasuringPointType.ASSEMBLY_OPERATION || type == MeasuringPointType.ENTRY_LEVEL_CALL) {
				if (point.getServiceId() != null) {
					DoubleMetricValue valueRelDist = null;
					// absolute dist
					double valueAbsDist = StatUtils.mean(point.getMonitoringDistribution().yAxis())
							- StatUtils.mean(point.getAnalysisDistribution().yAxis());

					// check the metric
					for (ValidationMetricValue metric : point.getMetricValues()) {
						if (metric.type() == ValidationMetricType.AVG_DISTANCE_REL) {
							valueRelDist = ((DoubleMetricValue) metric);
						}
					}

					if (valueRelDist != null) {
						double relDist = (double) valueRelDist.value();

						if (relDist >= THRES_REL_DIST) {
							if (valueAbsDist > 0) {
								adjustService(point.getServiceId(), ADJUSTMENT_FACTOR);
							} else if (valueAbsDist < 0) {
								adjustService(point.getServiceId(), -ADJUSTMENT_FACTOR);
							}
						}
					}
				}
			}
		});
	}

	private void adjustService(String service, double factor) {
		if (!currentValidationAdjustment.containsKey(service)) {
			currentValidationAdjustment.put(service, factor);
			currentServiceAdjustment.put(service, Math.signum(factor) * 1.0d);
		} else {
			double adjustmentBefore = currentServiceAdjustment.get(service);
			double adjustmentNow;
			if (Math.signum(adjustmentBefore) != Math.signum(factor)) {
				adjustmentNow = adjustmentBefore * -0.5d;
			} else {
				adjustmentNow = Math.signum(factor) * 1.0d;
			}

			currentValidationAdjustment.put(service,
					currentValidationAdjustment.get(service) + factor * Math.abs(adjustmentNow));
			currentServiceAdjustment.put(service, adjustmentNow);
		}
	}

}
