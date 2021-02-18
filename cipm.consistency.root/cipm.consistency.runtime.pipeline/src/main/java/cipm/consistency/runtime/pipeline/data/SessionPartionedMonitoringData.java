package cipm.consistency.runtime.pipeline.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.RecordWithSession;

public class SessionPartionedMonitoringData extends PartitionedMonitoringData<PCMContextRecord> {

	public SessionPartionedMonitoringData(List<PCMContextRecord> records, float validationSplit) {
		super(records, validationSplit);
	}

	@Override
	protected void prepareMonitoringData(List<PCMContextRecord> records, float validationSplit) {
		// 1. cluster to sessions
		Map<String, List<PCMContextRecord>> sessionToRecordsMapping = Maps.newHashMap();
		records.stream().forEach(rec -> {
			if (rec instanceof RecordWithSession) {
				String sessionId = ((RecordWithSession) rec).getSessionId();
				if (!sessionToRecordsMapping.containsKey(sessionId)) {
					sessionToRecordsMapping.put(sessionId, Lists.newArrayList());
				}
				sessionToRecordsMapping.get(sessionId).add(rec);
			} else {
				this.trainingData.add(rec);
				this.validationData.add(rec);
			}
		});

		// 2. decide sessions -> set mapping
		int numTrainingSessions = Math.round((float) sessionToRecordsMapping.size() * (1f - validationSplit));
		List<String> randomizedKeySet = Lists.newArrayList(sessionToRecordsMapping.keySet());
		Collections.shuffle(randomizedKeySet);
		List<List<PCMContextRecord>> sessionList = randomizedKeySet.stream().map(s -> sessionToRecordsMapping.get(s))
				.collect(Collectors.toList());
		for (int i = 0; i < sessionList.size(); i++) {
			if (i < numTrainingSessions) {
				sessionList.get(i).forEach(r -> this.trainingData.add(r));
			} else {
				sessionList.get(i).forEach(r -> this.validationData.add(r));
			}
		}
	}

}
