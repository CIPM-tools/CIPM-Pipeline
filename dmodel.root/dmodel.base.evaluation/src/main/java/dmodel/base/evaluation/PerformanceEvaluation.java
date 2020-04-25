package dmodel.base.evaluation;

import org.springframework.stereotype.Service;

import dmodel.base.core.evaluation.ExecutionMeasuringPoint;
import dmodel.base.core.health.HealthState;
import dmodel.base.evaluation.data.ExecutionData;
import dmodel.base.evaluation.data.PerformanceData;

@Service
public class PerformanceEvaluation {
	private PerformanceData data;
	private ExecutionData currentTrack;

	private long start;

	public PerformanceEvaluation() {
		data = new PerformanceData();
	}

	public void trackMeasuringPoint(ExecutionMeasuringPoint point) {
		currentTrack.trackPoint(point);
	}

	public void enterPipelineExecution() {
		currentTrack = new ExecutionData();
		currentTrack.setStartTime(System.currentTimeMillis());
		start = System.nanoTime();
	}

	public void exitPipelineExecution(HealthState success) {
		if (currentTrack != null) {
			currentTrack.setExecutionTimeCumulated(System.nanoTime() - start);
			currentTrack.setSuccess(success);
			data.getExecutionData().add(currentTrack);
			currentTrack = null;
		}
	}

	public PerformanceData getData() {
		return data;
	}

	public void trackPath(boolean b) {
		currentTrack.setExecutedPath(b);
	}

	public void trackUsageScenarios(int size) {
		currentTrack.setUsageScenarios(size);
	}

	public void trackRecordCount(int count) {
		currentTrack.setRecordCount(count);
	}

}
