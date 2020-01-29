package dmodel.pipeline.rt.pcm.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private static final double ADDITIVE_INCREASE = 0.02d;
	private static final double MULTIPLE_DECREASE = 0.5d;

	private static final double THRES_REL_DIST = 0.1d;

	private final LoopEstimationImpl loopEstimation;
	private final BranchEstimationImpl branchEstimation;

	private Map<String, Double> currentValidationAdjustment = Maps.newHashMap();
	private Map<String, Double> currentValidationAdjustmentGradient = Maps.newHashMap();

	public RepositoryDerivation() {
		this.loopEstimation = new LoopEstimationImpl();
		this.branchEstimation = new BranchEstimationImpl();
	}

	public void calibrateRepository(List<PCMContextRecord> data, InMemoryPCM pcm, PalladioRuntimeMapping mapping,
			ValidationData validation, Set<String> toPrepare) {
		try {
			prepareAdjustment(validation, toPrepare);
			java.lang.System.out.println(currentValidationAdjustment);

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

	private void prepareAdjustment(ValidationData validation, Set<String> fineGrainedInstrumentedServices) {
		if (validation == null) {
			return;
		}

		validation.getValidationPoints().forEach(point -> {
			MeasuringPointType type = point.getMeasuringPoint().getType();
			if (type == MeasuringPointType.ASSEMBLY_OPERATION || type == MeasuringPointType.ENTRY_LEVEL_CALL) {
				if (point.getServiceId() != null && point.getMonitoringDistribution() != null) {
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

					java.lang.System.out.println(valueAbsDist);
					if (valueRelDist != null) {
						double relDist = (double) valueRelDist.value();

						if (relDist >= THRES_REL_DIST
								&& fineGrainedInstrumentedServices.contains(point.getServiceId())) {
							if (valueAbsDist > 0) {
								adjustService(point.getServiceId(), true);
							} else if (valueAbsDist < 0) {
								adjustService(point.getServiceId(), false);
							}
						}
					}
				}
			}
		});
	}

	private void adjustService(String service, boolean scaleUp) {
		if (!currentValidationAdjustment.containsKey(service)) {
			currentValidationAdjustment.put(service, ADJUSTMENT_FACTOR * (scaleUp ? 1 : -1));
			currentValidationAdjustmentGradient.put(service, ADJUSTMENT_FACTOR);
		} else {
			double adjustmentBefore = currentValidationAdjustment.get(service);
			double currentGradient = currentValidationAdjustmentGradient.get(service);

			double adjustmentNow;
			if (scaleUp && adjustmentBefore < 0 || !scaleUp && adjustmentBefore > 0) {
				adjustmentNow = currentGradient * MULTIPLE_DECREASE;
			} else {
				adjustmentNow = currentGradient + ADDITIVE_INCREASE;
			}

			currentValidationAdjustment.put(service, adjustmentNow * Math.signum(adjustmentBefore) + adjustmentBefore);
		}
	}

}
