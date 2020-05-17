package dmodel.runtime.pipeline.scalability.generator;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import dmodel.designtime.monitoring.controller.IDFactory;
import dmodel.designtime.monitoring.controller.ServiceParameters;
import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class ServiceCallGenerator extends AbstractMonitoringDataGenerator {
	private static final IDFactory factory = new IDFactory();
	private static final Random RANDOM = new Random();

	@Builder.Default
	private String hostId = "";

	@Builder.Default
	private String hostName = "";

	@Builder.Default
	private List<String> serviceIds = Lists.newArrayList();

	@Builder.Default
	private ServiceParameters parameters = new ServiceParameters();

	@Builder.Default
	private Optional<String> externalCallId = Optional.empty();

	@Builder.Default
	private long entryTime = 0;

	@Builder.Default
	private long exitTime = 0;

	@Override
	protected Pair<PCMContextRecord, Optional<String>> generateRecord(String sessionId, Optional<String> parentId) {
		String externalCallIdGen = externalCallId.orElse(ServiceCallRecord.EXTERNAL_CALL_ID);
		String genId = factory.createId();
		String genServiceId = serviceIds.get(RANDOM.nextInt(serviceIds.size()));

		return Pair.of(new ServiceCallRecord(sessionId, genId, hostId, hostName, genServiceId, parameters.toString(),
				parentId.orElse(null), externalCallIdGen, null, entryTime, exitTime), Optional.of(genId));
	}

}
