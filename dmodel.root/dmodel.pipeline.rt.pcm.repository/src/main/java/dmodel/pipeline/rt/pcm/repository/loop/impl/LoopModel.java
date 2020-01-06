package dmodel.pipeline.rt.pcm.repository.loop.impl;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;

/**
 * Estimated model for a loop.
 * 
 * @author JP
 *
 */
public interface LoopModel {

	/**
	 * Predicts the number of loop iterations for this loop based on a service call
	 * context.
	 * 
	 * @param serviceCall Context information, like service call parameters and
	 *                    service execution ID.
	 * @return A predicted number of loop iterations for this loop.
	 */
	double predictIterations(ServiceCallRecord serviceCall);

	/**
	 * Gets the stochastic expression of this estimated model.
	 * 
	 * @return The stochastic expression string.
	 */
	String getIterationsStochasticExpression();

}