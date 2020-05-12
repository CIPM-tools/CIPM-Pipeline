package dmodel.base.evaluation.data;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Characterizes an interval with start and stop timestamp. The start and the
 * stop are optionals, so they could also be empty.
 * 
 * @author David Monschein
 *
 */
@Data
public class StartStopInterval {

	@JsonIgnore
	private Optional<Long> start = Optional.empty();

	@JsonIgnore
	private Optional<Long> stop = Optional.empty();

	/**
	 * Creates a new interval and sets the current nano timestamp as start, if
	 * specified.
	 * 
	 * @param init true if the current nano timestamp should be set as start
	 */
	public StartStopInterval(boolean init) {
		if (init) {
			start = Optional.of(System.nanoTime());
		}
	}

	/**
	 * Sets the current nano timestamp as start of the interval.
	 */
	public void start() {
		start = Optional.of(System.nanoTime());
	}

	/**
	 * Sets the current nano timestamp as end of the interval.
	 */
	public void stop() {
		stop = Optional.of(System.nanoTime());
	}

	/**
	 * Creates a new interval with the current nano timestamp as start.
	 */
	public StartStopInterval() {
		this(true);
	}

	/**
	 * Determines whether the start timestamp of the interval is present.
	 * 
	 * @return true if the start timestamp is present, false otherwise
	 */
	public boolean startPresent() {
		return start != null && start.isPresent();
	}

	/**
	 * Determines whether the stop timestamp of the interval is present.
	 * 
	 * @return true if the stop timestamp is present, false otherwise
	 */
	public boolean stopPresent() {
		return stop != null && stop.isPresent();
	}

	/**
	 * Determines whether start and stop timestamp are present. This is a shortcut
	 * for {@link StartStopInterval#startPresent()} in combination with
	 * {@link StartStopInterval#stopPresent()}.
	 * 
	 * @return
	 */
	public boolean completed() {
		return startPresent() && stopPresent();
	}

	/**
	 * Gets the duration of the interval if it is completed (zero otherwise).
	 * 
	 * @return duration of the interval (stop - start) or zero if it is not
	 *         completed.
	 */
	@JsonProperty("duration")
	public long getDuration() {
		if (!completed()) {
			return 0;
		}

		return stop.get() - start.get();
	}

}
