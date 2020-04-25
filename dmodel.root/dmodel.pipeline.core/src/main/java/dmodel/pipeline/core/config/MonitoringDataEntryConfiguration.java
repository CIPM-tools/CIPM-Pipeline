package dmodel.pipeline.core.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * Configuration of the monitoring data collection.
 * 
 * @author David Monschein
 *
 */
@Data
public class MonitoringDataEntryConfiguration {
	/**
	 * Size of the sliding window in seconds.
	 */
	private long slidingWindowSize = 3600; // 1 hour

	/**
	 * Interval of triggering the pipeline with the current monitoring data in the
	 * sliding window (in seconds).
	 */
	private long slidingWindowTrigger = 300; // 5 minutes

	/**
	 * Determines whether the values are valid.
	 * 
	 * @return true, if the values are valid (both not zero and positive), false
	 *         otherwise
	 */
	@JsonIgnore
	public boolean isValid() {
		return slidingWindowSize > 0 && slidingWindowTrigger > 0;
	}

}
