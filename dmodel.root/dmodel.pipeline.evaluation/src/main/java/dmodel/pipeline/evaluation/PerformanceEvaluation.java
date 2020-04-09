package dmodel.pipeline.evaluation;

import org.springframework.stereotype.Service;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import dmodel.pipeline.evaluation.data.ExecutionData;
import dmodel.pipeline.evaluation.data.PerformanceData;

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

	public void exitPipelineExecution() {
		if (currentTrack != null) {
			currentTrack.setExecutionTimeCumulated(System.nanoTime() - start);
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
