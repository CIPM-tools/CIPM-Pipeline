package dmodel.base.evaluation.data;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StartStopInterval {

	@JsonIgnore
	private Optional<Long> start = Optional.empty();

	@JsonIgnore
	private Optional<Long> stop = Optional.empty();

	public StartStopInterval(boolean init) {
		if (init) {
			start = Optional.of(System.nanoTime());
		}
	}

	public void start() {
		start = Optional.of(System.nanoTime());
	}

	public void stop() {
		stop = Optional.of(System.nanoTime());
	}

	public StartStopInterval() {
		this(true);
	}

	public boolean startPresent() {
		return start != null && start.isPresent();
	}

	public boolean stopPresent() {
		return stop != null && stop.isPresent();
	}

	public boolean completed() {
		return startPresent() && stopPresent();
	}

	@JsonProperty("duration")
	public long getDuration() {
		return stop.get() - start.get();
	}

}
