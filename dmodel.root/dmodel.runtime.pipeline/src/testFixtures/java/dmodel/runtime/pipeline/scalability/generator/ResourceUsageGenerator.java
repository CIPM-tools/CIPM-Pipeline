package dmodel.runtime.pipeline.scalability.generator;

import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.designtime.monitoring.records.ResourceUtilizationRecord;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class ResourceUsageGenerator extends AbstractMonitoringDataGenerator {
	private static final Random RANDOM = new Random();

	@Builder.Default
	private Optional<StartStopTimeGenerator> intervalGenerator = Optional.empty();

	@Builder.Default
	private long entryTime = 0;

	@Builder.Default
	private double usageMin = 0;

	@Builder.Default
	private double usageMax = 0;

	@Builder.Default
	private String hostId = "";

	@Builder.Default
	private String hostName = "";

	@Builder.Default
	private String resourceId = "";

	@Override
	protected Pair<PCMContextRecord, Optional<String>> generateRecord(String sessionId, Optional<String> parentId) {
		long entryTime;
		if (intervalGenerator.isPresent()) {
			Pair<Long, Long> gen = this.intervalGenerator.get().generateNextInterval();
			entryTime = gen.getLeft();
		} else {
			entryTime = this.entryTime;
		}

		double randomUsage = usageMin + (usageMax - usageMin) * RANDOM.nextDouble();

		return Pair.of(new ResourceUtilizationRecord(hostId, hostName, resourceId, randomUsage, entryTime),
				Optional.empty());
	}

}
