package dmodel.runtime.pipeline.pcm.repository.loop;

import org.palladiosimulator.pcm.seff.LoopAction;

import dmodel.designtime.monitoring.records.ServiceCallRecord;

/**
 * Interface for general loop prediction.
 * 
 * @author JP
 *
 */
public interface LoopPrediction {

	/**
	 * Gets a {@link LoopAction} iteration prediction for a loop and service call.
	 * 
	 * @param loop        The loop a prediction is made for.
	 * @param serviceCall Context information, like service call parameters and
	 *                    service execution ID.
	 * @return The predicted loop iterations.
	 */
	double estimateIterations(LoopAction loop, ServiceCallRecord serviceCall);

}