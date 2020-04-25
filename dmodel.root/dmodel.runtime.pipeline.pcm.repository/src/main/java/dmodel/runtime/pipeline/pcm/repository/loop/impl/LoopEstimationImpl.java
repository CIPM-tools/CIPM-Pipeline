package dmodel.runtime.pipeline.pcm.repository.loop.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.LoopAction;

import dmodel.base.shared.ModelUtil;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.runtime.pipeline.pcm.repository.MonitoringDataSet.Loops;
import dmodel.runtime.pipeline.pcm.repository.MonitoringDataSet.ServiceCalls;
import dmodel.runtime.pipeline.pcm.repository.loop.LoopEstimation;
import dmodel.runtime.pipeline.pcm.repository.loop.LoopPrediction;

/**
 * Implements loop estimation and prediction by using
 * {@link WekaLoopModelEstimation}.
 * 
 * @author JP
 *
 */
public class LoopEstimationImpl implements LoopEstimation, LoopPrediction {

	private static final Logger LOGGER = Logger.getLogger(LoopEstimationImpl.class);
	private final Map<String, LoopModel> modelCache;

	/**
	 * Initializes a new instance of {@link LoopEstimationImpl}.
	 */
	public LoopEstimationImpl() {
		this.modelCache = new HashMap<>();
	}

	@Override
	public double estimateIterations(final LoopAction loop, final ServiceCallRecord serviceCall) {
		LoopModel loopModel = this.modelCache.get(loop.getId());
		if (loopModel == null) {
			return 0;
		}
		return loopModel.predictIterations(serviceCall);
	}

	@Override
	public void update(final Repository pcmModel, final ServiceCalls serviceCalls, final Loops loopIterations) {

		WekaLoopModelEstimation estimation = new WekaLoopModelEstimation(serviceCalls, loopIterations);

		Map<String, LoopModel> loopModels = estimation.estimateAll();

		this.modelCache.putAll(loopModels);

		this.applyEstimations(pcmModel);
	}

	private void applyEstimations(final Repository pcmModel) {
		List<LoopAction> loops = ModelUtil.getObjects(pcmModel, LoopAction.class);
		for (LoopAction loopAction : loops) {
			this.applyModel(loopAction);
		}
	}

	private void applyModel(final LoopAction loop) {
		LoopModel loopModel = this.modelCache.get(loop.getId());
		if (loopModel == null) {
			LOGGER.warn(
					"A estimation for loop with id " + loop.getId() + " was not found. Nothing is set for this loop.");
			return;
		}
		String stoEx = loopModel.getIterationsStochasticExpression();
		PCMRandomVariable randomVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		randomVariable.setSpecification(stoEx);
		loop.setIterationCount_LoopAction(randomVariable);
	}
}
