package dmodel.base.core.facade;

import dmodel.base.core.evaluation.ExecutionMeasuringPoint;
import dmodel.base.core.health.HealthState;
import dmodel.base.core.state.EPipelineTransformation;
import dmodel.base.core.state.ETransformationState;

/**
 * Facade which provides core functionalities for the transformations within the
 * pipeline.
 * 
 * @author David Monschein
 *
 */
public interface ICoreBlackboardQueryFacade extends IResettableQueryFacade {

	/**
	 * Updates the state of a specific transformation within the pipeline.
	 * 
	 * @param transformation the transformation whose status has changed
	 * @param state          the new state of the transformation
	 */
	public void updateState(EPipelineTransformation transformation, ETransformationState state);

	// performance tracking events
	/**
	 * Tracks the start of the pipeline.
	 */
	public void trackStartPipelineExecution();

	/**
	 * Tracks the end of the pipeline execution.
	 * 
	 * @param success the status which indicates whether the pipeline execution
	 *                worked flawlessly.
	 */
	public void trackEndPipelineExecution(HealthState success);

	/**
	 * Keeps track of an logical point in the execution path of the pipeline and
	 * measures the execution time for evaluation purposes.
	 * 
	 * @param point the point to keep track of
	 */
	public void track(ExecutionMeasuringPoint point);

	/**
	 * Keeps track of the amount of monitoring records that are passed to the
	 * pipeline.
	 * 
	 * @param count number of monitoring records
	 */
	public void trackRecordCount(int count);

	/**
	 * Saves which path was taken within the pipeline.
	 * 
	 * @param b true if the repository transformation has been executed twice, false
	 *          if the usage model transformation has been executed twice
	 */
	public void trackPath(boolean b);

	/**
	 * Saves the number of usage scenarios that have been extracted from the
	 * monitoring data.
	 * 
	 * @param scenarioCount number of usage scenarios that have been extracted from
	 *                      the monitoring data
	 */
	public void trackUsageScenarios(int scenarioCount);

}
