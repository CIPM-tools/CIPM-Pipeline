package dmodel.pipeline.rt.entry.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "entry")
public class MonitoringDataEntryConfiguration {

	private long slidingWindowSize;
	private long slidingWindowTrigger;

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
