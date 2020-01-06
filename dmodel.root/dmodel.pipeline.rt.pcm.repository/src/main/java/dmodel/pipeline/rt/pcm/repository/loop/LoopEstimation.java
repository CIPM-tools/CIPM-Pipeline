package dmodel.pipeline.rt.pcm.repository.loop;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.LoopAction;

import dmodel.pipeline.monitoring.records.LoopRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.repository.MonitoringDataSet.Loops;
import dmodel.pipeline.rt.pcm.repository.MonitoringDataSet.ServiceCalls;

/**
 * Interface for loop iteration estimation implementations, which use
 * {@link ServiceCallRecord} and {@link LoopRecord} to estimate loop iterations
 * and update the PCM.
 * 
 * @author JP
 *
 */
public interface LoopEstimation {

	/**
	 * Updates the stochastic expressions of loop iterations of {@link LoopAction},
	 * based on the monitored service call parameters and loop records.
	 * 
	 * @param pcmModel       The model which will be updated.
	 * @param serviceCalls   The monitored service calls, including service call
	 *                       parameters.
	 * @param loopIterations The monitored loop records.
	 */
	void update(Repository pcmModel, ServiceCalls serviceCalls, Loops loopIterations);
}