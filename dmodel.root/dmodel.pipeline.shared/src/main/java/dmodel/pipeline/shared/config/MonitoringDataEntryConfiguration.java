package dmodel.pipeline.shared.config;

import lombok.Data;

@Data
public class MonitoringDataEntryConfiguration {

	private long slidingWindowSize = 3600; // 1 hour
	private long slidingWindowTrigger = 300; // 5 minutes

}
