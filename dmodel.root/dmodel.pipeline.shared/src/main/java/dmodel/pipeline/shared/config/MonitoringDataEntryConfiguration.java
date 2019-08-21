package dmodel.pipeline.shared.config;

public class MonitoringDataEntryConfiguration {

	private long slidingWindowSize = 3600; // 1 hour
	private long slidingWindowTrigger = 300; // 5 minutes

	public long getSlidingWindowSize() {
		return slidingWindowSize;
	}

	public void setSlidingWindowSize(long slidingWindowSize) {
		this.slidingWindowSize = slidingWindowSize;
	}

	public long getSlidingWindowTrigger() {
		return slidingWindowTrigger;
	}

	public void setSlidingWindowTrigger(long slidingWindowTrigger) {
		this.slidingWindowTrigger = slidingWindowTrigger;
	}

}
