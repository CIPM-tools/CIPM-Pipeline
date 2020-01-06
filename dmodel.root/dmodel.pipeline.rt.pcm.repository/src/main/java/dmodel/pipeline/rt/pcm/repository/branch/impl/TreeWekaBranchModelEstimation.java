package dmodel.pipeline.rt.pcm.repository.branch.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import dmodel.pipeline.monitoring.records.BranchRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.repository.MonitoringDataSet.Branches;
import dmodel.pipeline.rt.pcm.repository.MonitoringDataSet.ServiceCalls;
import dmodel.pipeline.rt.pcm.repository.data.WekaDataSet;
import dmodel.pipeline.rt.pcm.repository.data.WekaDataSetBuilder;
import dmodel.pipeline.rt.pcm.repository.data.WekaDataSetMode;

/**
 * Implements the branch model estimation by using a J48 tree from the weka
 * library. The tree can then be transformed into a stochastic expression. We
 * can not use logistic regression as estimation method, because the
 * expressiveness of the stochastic expressions does not suffice.
 * 
 * @author JP
 *
 */
public class TreeWekaBranchModelEstimation {

	private final ServiceCalls serviceCalls;

	private final Branches branchExecutions;

	private final Random random;

	/**
	 * Initializes a new instance of {@link TreeWekaBranchModelEstimation}.
	 * 
	 * @param serviceCalls     The service call data set.
	 * @param branchExecutions The branch record data set.
	 */
	public TreeWekaBranchModelEstimation(final ServiceCalls serviceCalls, final Branches branchExecutions) {
		this(serviceCalls, branchExecutions, ThreadLocalRandom.current());
	}

	/**
	 * Initializes a new instance of {@link TreeWekaBranchModelEstimation}.
	 * 
	 * @param serviceCalls     The service call data set.
	 * @param branchExecutions The branch record data set.
	 * @param random           The prediction needs a random number. Define
	 *                         {@link Random} with a constant seed to obtain a
	 *                         deterministic result.
	 */
	public TreeWekaBranchModelEstimation(final ServiceCalls serviceCalls, final Branches branchExecutions,
			final Random random) {
		this.serviceCalls = serviceCalls;
		this.branchExecutions = branchExecutions;
		this.random = random;
	}

	/**
	 * Gets a branch model for a specific branch id.
	 * 
	 * @param branchId The id of the branch the model is build for.
	 * @return The estimated branch model.
	 */
	public BranchModel estimate(final String branchId) {
		try {
			return this.internEstimate(branchId);
		} catch (Exception e) {
			throw new RuntimeException("Error estimating branch with id " + branchId, e);
		}
	}

	/**
	 * Get for each branch in the {@link BranchDataSet} a branch model.
	 * 
	 * @return A map, which maps branch IDs to their corresponding branch model.
	 */
	public Map<String, BranchModel> estimateAll() {
		Map<String, BranchModel> returnValue = new HashMap<>();
		for (String branchId : this.branchExecutions.getBranchIds()) {
			returnValue.put(branchId, this.estimate(branchId));
		}
		return returnValue;
	}

	private BranchModel estimate(final String branchId, final Set<String> branchExecutionIds) throws Exception {
		List<BranchRecord> records = this.branchExecutions.getBranchRecords(branchId);

		if (records.size() == 0) {
			throw new IllegalStateException("No records for branch id " + branchId + " found.");
		}

		// Check if every time the same branch is executed. Weka cannot handle unary
		// class attributes.
		Set<String> branchExecutedLabels = branchExecutionIds.stream().collect(Collectors.toSet());
		if (branchExecutedLabels.size() == 0) {
			return new ConstantBranchModel(Optional.empty());
		} else if (branchExecutedLabels.size() == 1) {
			return new ConstantBranchModel(Optional.of(branchExecutedLabels.stream().iterator().next()));
		}

		WekaDataSetBuilder<String> dataSetBuilder = new WekaDataSetBuilder<>(this.serviceCalls,
				WekaDataSetMode.NoTransformations);

		for (BranchRecord record : records) {
			dataSetBuilder.addInstance(record.getServiceExecutionId(), record.getExecutedBranchId());
		}

		WekaDataSet<String> dataset = dataSetBuilder.build();

		return new WekaBranchModel(dataset, this.random, this.branchExecutions.getBranchNotExecutedId());
	}

	private BranchModel internEstimate(final String branchId) throws Exception {
		List<BranchRecord> records = this.branchExecutions.getBranchRecords(branchId);

		if (records.size() == 0) {
			throw new IllegalStateException("No records for branch id " + branchId + " found.");
		}

		Set<String> executedBranchIds = new HashSet<>();

		for (BranchRecord record : records) {
			executedBranchIds.add(record.getExecutedBranchId());
		}

		return this.estimate(branchId, executedBranchIds);
	}

	private static class ConstantBranchModel implements BranchModel {

		private final Optional<String> transitionId;

		public ConstantBranchModel(final Optional<String> transitionId) {
			this.transitionId = transitionId;
		}

		@Override
		public Optional<String> predictBranchId(final ServiceCallRecord serviceCall) {
			return this.transitionId;
		}

		@Override
		public String getBranchStochasticExpression(final String transitionId) {
			if (this.transitionId.isPresent() && this.transitionId.get().equals(transitionId)) {
				return "true";
			} else {
				return "false";
			}
		}

	}
}
