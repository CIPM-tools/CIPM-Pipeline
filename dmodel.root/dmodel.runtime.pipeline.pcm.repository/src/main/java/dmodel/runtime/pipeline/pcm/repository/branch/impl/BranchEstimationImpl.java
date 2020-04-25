package dmodel.runtime.pipeline.pcm.repository.branch.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.GuardedBranchTransition;

import dmodel.base.shared.ModelUtil;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.runtime.pipeline.pcm.repository.MonitoringDataSet.Branches;
import dmodel.runtime.pipeline.pcm.repository.MonitoringDataSet.ServiceCalls;
import dmodel.runtime.pipeline.pcm.repository.branch.BranchEstimation;
import dmodel.runtime.pipeline.pcm.repository.branch.BranchPrediction;

/**
 * Implements branch estimation and prediction by using
 * {@link TreeWekaBranchModelEstimation}.
 * 
 * @author JP
 *
 */
public class BranchEstimationImpl implements BranchEstimation, BranchPrediction {

	private static final Logger LOGGER = Logger.getLogger(BranchEstimationImpl.class);

	private final Map<String, BranchModel> modelCache;
	private final Random random;

	/**
	 * Initializes a new instance of {@link BranchEstimationImpl}.
	 */
	public BranchEstimationImpl() {
		this(ThreadLocalRandom.current());
	}

	/**
	 * Initializes a new instance of {@link BranchEstimationImpl}.
	 * 
	 * @param random The prediction may need a random number. Define {@link Random}
	 *               with a constant seed to obtain a deterministic result.
	 */
	public BranchEstimationImpl(final Random random) {
		this.modelCache = new HashMap<>();
		this.random = random;
	}

	@Override
	public Optional<AbstractBranchTransition> predictTransition(final BranchAction branch,
			final ServiceCallRecord serviceCall) {
		BranchModel branchModel = this.modelCache.get(branch.getId());
		if (branchModel == null) {
			return Optional.empty();
		}
		Optional<String> estimatedBranchId = branchModel.predictBranchId(serviceCall);

		if (estimatedBranchId.isPresent() == false) {
			return Optional.empty();
		}

		Optional<AbstractBranchTransition> estimatedBranch = branch.getBranches_Branch().stream()
				.filter(transition -> transition.getId().equals(estimatedBranchId.get())).findFirst();

		if (estimatedBranch.isPresent() == false) {
			throw new IllegalArgumentException(
					"The estimated branch transition with id " + estimatedBranchId.get() + " does not exist in SEFF.");
		}

		return Optional.of(estimatedBranch.get());
	}

	@Override
	public void update(final Repository pcmModel, final ServiceCalls serviceCalls, final Branches branchExecutions) {
		TreeWekaBranchModelEstimation estimation = new TreeWekaBranchModelEstimation(serviceCalls, branchExecutions,
				this.random);

		Map<String, BranchModel> branchModels = estimation.estimateAll();

		this.modelCache.putAll(branchModels);

		this.applyEstimations(pcmModel);
	}

	/**
	 * Gets the branch model for a branch id.
	 * 
	 * @param branchId The id of the branch, the model is created for.
	 * @return The branch model or empty.
	 */
	public Optional<BranchModel> get(String branchId) {
		return Optional.ofNullable(this.modelCache.get(branchId));
	}

	private void applyEstimations(final Repository pcmModel) {
		List<BranchAction> branches = ModelUtil.getObjects(pcmModel, BranchAction.class);
		for (BranchAction branch : branches) {
			this.applyModel(branch);
		}
	}

	private void applyModel(final BranchAction branch) {
		for (AbstractBranchTransition branchTransition : branch.getBranches_Branch()) {
			if (branchTransition instanceof GuardedBranchTransition) {
				this.applyModel(branch.getId(), (GuardedBranchTransition) branchTransition);
			} else {
				LOGGER.warn("A estimation for transition " + branchTransition.getId() + " in branch with id "
						+ branch.getId()
						+ " is not of type GuardedBranchTransition. Nothing is set for this branch transition.");
			}
		}
	}

	private void applyModel(final String branchId, final GuardedBranchTransition branch) {
		BranchModel branchModel = this.modelCache.get(branchId);
		if (branchModel == null) {
			LOGGER.warn(
					"A estimation for branch with id " + branchId + " was not found. Nothing is set for this branch.");
			return;
		}
		String stoEx = branchModel.getBranchStochasticExpression(branch.getId());
		PCMRandomVariable randomVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		randomVariable.setSpecification(stoEx);
		branch.setBranchCondition_GuardedBranchTransition(randomVariable);
	}
}
