package cipm.consistency.base.evaluation;

import org.springframework.stereotype.Service;

import cipm.consistency.base.core.health.HealthState;
import cipm.consistency.base.core.state.ExecutionMeasuringPoint;
import cipm.consistency.base.evaluation.data.ExecutionData;
import cipm.consistency.base.evaluation.data.PerformanceData;

/**
 * Mainly used for evaluation purposes. Tracks the execution times of the
 * transformation pipeline and the transformations.
 * 
 * @author David Monschein
 *
 */
@Service
public class PerformanceEvaluation {
	/**
	 * Performance data as a whole.
	 */
	private PerformanceData data;

	/**
	 * Current pipeline execution that is tracked.
	 */
	private ExecutionData currentTrack;

	/**
	 * Start timestamp of a pipeline execution.
	 */
	private long start;

	/**
	 * Creates a new instance for tracking the pipelines transformation.
	 */
	public PerformanceEvaluation() {
		data = new PerformanceData();
	}

	/**
	 * Keeps track of a certain action (point) within the pipeline.
	 * 
	 * @param point the point to track
	 */
	public void trackMeasuringPoint(ExecutionMeasuringPoint point) {
		currentTrack.trackPoint(point);
	}

	/**
	 * Tracks the start of the pipeline execution.
	 */
	public void enterPipelineExecution() {
		currentTrack = new ExecutionData();
		currentTrack.setStartTime(System.currentTimeMillis());
		start = System.nanoTime();
	}

	/**
	 * Tracks the end of the pipeline execution and remembers the health state.
	 * 
	 * @param success whether the pipeline execution finished with errors or not
	 */
	public void exitPipelineExecution(HealthState success) {
		if (currentTrack != null) {
			currentTrack.setExecutionTimeCumulated(System.nanoTime() - start);
			currentTrack.setSuccess(success);
			data.getExecutionData().add(currentTrack);
			currentTrack = null;
		}
	}

	/**
	 * Gets the recorded performance data.
	 * 
	 * @return recorded performance data
	 */
	public PerformanceData getData() {
		return data;
	}

	/**
	 * Keeps track of the path of the pipeline execution
	 * 
	 * @param b true if the repository transformation was executed twice, false if
	 *          usage model transformation was executed twice
	 */
	public void trackPath(boolean b) {
		currentTrack.setExecutedPath(b);
	}

	/**
	 * Tracks the number of extracted usage scenarios.
	 * 
	 * @param size number of extracted usage scenarios
	 */
	public void trackUsageScenarios(int size) {
		currentTrack.setUsageScenarios(size);
	}

	/**
	 * Tracks the number of records passed to the pipeline.
	 * 
	 * @param count number of records passed to the pipeline
	 */
	public void trackRecordCount(int count) {
		currentTrack.setRecordCount(count);
	}

}
