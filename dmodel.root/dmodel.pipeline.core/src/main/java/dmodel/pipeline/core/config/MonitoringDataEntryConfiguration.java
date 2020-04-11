package dmodel.pipeline.core.config;

import lombok.Data;

@Data
public class MonitoringDataEntryConfiguration {

	private long slidingWindowSize = 3600; // 1 hour
	private long slidingWindowTrigger = 300; // 5 minutes

	public boolean isValid() {
		return slidingWindowSize > 0 && slidingWindowTrigger > 0;
	}

}
