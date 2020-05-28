package dmodel.runtime.pipeline.scalability.generator;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.designtime.monitoring.records.ResponseTimeRecord;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class InternalActionGenerator extends AbstractMonitoringDataGenerator {

	@Builder.Default
	private String internalActionId = "";

	@Builder.Default
	private String resourceId = "";

	@Builder.Default
	private long entryTime = 0;

	@Builder.Default
	private long exitTime = 0;

	@Builder.Default
	private Optional<StartStopTimeGenerator> intervalGenerator = Optional.empty();

	@Override
	protected Pair<PCMContextRecord, Optional<String>> generateRecord(String sessionId, Optional<String> parentId) {
		long entryTime, exitTime;
		if (intervalGenerator.isPresent()) {
			Pair<Long, Long> gen = this.intervalGenerator.get().generateNextInterval();
			entryTime = gen.getLeft();
			exitTime = gen.getRight();
		} else {
			entryTime = this.entryTime;
			exitTime = this.exitTime;
		}

		return Pair.of(new ResponseTimeRecord(sessionId, parentId.orElse(null), internalActionId, resourceId, entryTime,
				exitTime), Optional.empty());
	}

}
