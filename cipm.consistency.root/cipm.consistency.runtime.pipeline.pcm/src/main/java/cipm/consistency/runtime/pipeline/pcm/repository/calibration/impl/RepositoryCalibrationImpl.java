package cipm.consistency.runtime.pipeline.pcm.repository.calibration.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import cipm.consistency.base.core.config.ConfigurationContainer;
import cipm.consistency.base.core.facade.IPCMQueryFacade;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.pcm.repository.IRegressionOutlierDetection;
import cipm.consistency.runtime.pipeline.pcm.repository.IRepositoryCalibration;
import cipm.consistency.runtime.pipeline.pcm.repository.calibration.ExecutionTimesExtractor;
import cipm.consistency.runtime.pipeline.pcm.repository.calibration.ExecutionTimesExtractor.RegressionDataset;
import cipm.consistency.runtime.pipeline.pcm.repository.calibration.GeneralizationAwareRegression;
import cipm.consistency.runtime.pipeline.pcm.repository.calibration.RepositoryStoexChanges;
import cipm.consistency.runtime.pipeline.pcm.repository.outlier.NumericOutlierDetection;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;
import lombok.extern.java.Log;

@Component
@Log
public class RepositoryCalibrationImpl implements IRepositoryCalibration, InitializingBean {
	private ExecutionTimesExtractor executionTimesExtractor;
	private GeneralizationAwareRegression regression;
	private IRegressionOutlierDetection outlierDetection;

	private Map<String, RegressionDataset> existingRegressionSets;

	@Autowired
	private ConfigurationContainer config;

	public RepositoryCalibrationImpl() {
		executionTimesExtractor = new ExecutionTimesExtractor();
		regression = new GeneralizationAwareRegression();
		existingRegressionSets = Maps.newHashMap();
	}

	@Override
	public RepositoryStoexChanges calibrateRepository(List<PCMContextRecord> data, IPCMQueryFacade pcm,
			ValidationData validation, Set<String> fineGraindInstrumentedServiceIds) {
		log.info("Start resource demand calibration.");
		RepositoryStoexChanges derivedChanges = new RepositoryStoexChanges();

		log.info("Extract datasets.");
		List<RegressionDataset> nDatasets = executionTimesExtractor.mergeDatasets(existingRegressionSets, data);

		for (RegressionDataset dataset : nDatasets) {
			dataset.cutData(config.getCalibration().getRegressionHorizon());

			try {
				log.info("Filter outliers.");
				if (config.getCalibration().getOutlierPercentile() > 0) {
					outlierDetection.filterOutliers(dataset);
				}
				log.info("Perform regression.");
				PCMRandomVariable nStoexExpression = regression.performRegression(dataset, config.getCalibration());
				derivedChanges.put(dataset.getActionId(), nStoexExpression);

				// put it back
				existingRegressionSets.put(dataset.getActionId(), dataset);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		log.info("Finished resource demand calibration.");

		return derivedChanges;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		outlierDetection = new NumericOutlierDetection(config.getCalibration().getOutlierPercentile());
	}

	@Override
	public void reset() {
		this.existingRegressionSets.clear();
	}

}
