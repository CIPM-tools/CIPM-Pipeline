package dmodel.pipeline.evaluation;

import org.springframework.stereotype.Service;

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

	public long getTime() {
		return System.nanoTime();
	}

	public void trackPreFilter(long start) {
		currentTrack.setPreFilter(getTime() - start);
	}

	public void trackServiceCallTreeExtraction(long start) {
		currentTrack.setServiceCallTree(getTime() - start);
	}

	public void trackPreValidation(long start2) {
		currentTrack.setValidation1(getTime() - start2);
	}

	public void trackResourceEnvironment(long start) {
		currentTrack.setResourceEnvironmentUpdates(getTime() - start);
	}

	public void trackSystem(long start) {
		currentTrack.setSystemUpdates(getTime() - start);
	}

	public void trackCalibration1(long start) {
		currentTrack.setCalibration1(getTime() - start);
	}

	public void trackCalibration2(long start) {
		currentTrack.setCalibration2(getTime() - start);
	}

	public void trackUsage1(long start) {
		currentTrack.setUsage1(getTime() - start);
	}

	public void trackUsage2(long start) {
		currentTrack.setUsage2(getTime() - start);
	}

	public void trackValidationRepository(long start) {
		currentTrack.setValidation2(getTime() - start);
	}

	public void trackValidationUsage(long start) {
		currentTrack.setValidation3(getTime() - start);
	}

	public void trackFinalValidation(long start) {
		currentTrack.setValidation4(getTime() - start);
	}

	public void trackInstrumentationModel(long start) {
		currentTrack.setInstrumentationModel(getTime() - start);
	}

	public void trackCrossValidation(long start) {
		currentTrack.setCrossValidation(getTime() - start);
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
		currentTrack.setPath(b);
	}

	public void trackUsageScenarios(int size) {
		currentTrack.setUsageScenarios(size);
	}

	public void trackRecordCount(int count) {
		currentTrack.setRecordCount(count);
	}

}
