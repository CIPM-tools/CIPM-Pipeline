package dmodel.pipeline.rt.pcm.repository.loop.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmodel.pipeline.monitoring.records.LoopRecord;
import dmodel.pipeline.rt.pcm.repository.MonitoringDataSet.Loops;
import dmodel.pipeline.rt.pcm.repository.MonitoringDataSet.ServiceCalls;
import dmodel.pipeline.rt.pcm.repository.data.WekaDataSetBuilder;
import dmodel.pipeline.rt.pcm.repository.data.WekaDataSetMode;

/**
 * Implements the loop model estimation by using linear regression from the weka
 * library. This does not imply that only linear dependencies can be detected,
 * because we present different pre-defined possible dependency relations, such
 * as a quadratic dependency, as input. The linear regression then finds the
 * best candidates.
 * 
 * @author JP
 *
 */
public class WekaLoopModelEstimation {

	private final ServiceCalls serviceCalls;

	private final Loops loopIterations;

	/**
	 * Initializes a new instance of {@link WekaLoopModelEstimation}.
	 * 
	 * @param serviceCalls   The service call data set.
	 * @param loopIterations The loop record data set.
	 */
	public WekaLoopModelEstimation(final ServiceCalls serviceCalls, final Loops loopIterations) {
		this.serviceCalls = serviceCalls;
		this.loopIterations = loopIterations;
	}

	/**
	 * Gets a loop model for a specific loop id.
	 * 
	 * @param loopId The id of the loop the model is build for.
	 * @return The estimated loop model.
	 */
	public LoopModel estimate(final String loopId) {
		try {
			return this.internEstimate(loopId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets for each loop in the {@link LoopDataSet} a loop model.
	 * 
	 * @return A map, which maps loop IDs to their corresponding loop model.
	 */
	public Map<String, LoopModel> estimateAll() {
		HashMap<String, LoopModel> returnValue = new HashMap<>();
		for (String loopId : this.loopIterations.getLoopIds()) {
			returnValue.put(loopId, this.estimate(loopId));
		}
		return returnValue;
	}

	private LoopModel internEstimate(final String loopId) throws Exception {
		List<LoopRecord> records = this.loopIterations.getLoopRecords(loopId);

		if (records.size() == 0) {
			throw new IllegalStateException("No records for loop id " + loopId + " found.");
		}

		WekaDataSetBuilder<Long> dataSetBuilder = new WekaDataSetBuilder<Long>(this.serviceCalls,
				WekaDataSetMode.IntegerOnly);

		for (LoopRecord record : records) {
			dataSetBuilder.addInstance(record.getServiceExecutionId(), record.getLoopIterationCount());
		}

		return new WekaLoopModel(dataSetBuilder.build());
	}
}
