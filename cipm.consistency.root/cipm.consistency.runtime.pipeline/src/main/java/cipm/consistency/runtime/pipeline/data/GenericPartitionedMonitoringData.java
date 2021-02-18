package cipm.consistency.runtime.pipeline.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kieker.common.record.IMonitoringRecord;

public class GenericPartitionedMonitoringData extends PartitionedMonitoringData<IMonitoringRecord> {

	public GenericPartitionedMonitoringData(List<IMonitoringRecord> records, float validationSplit) {
		super(records, validationSplit);
	}

	@Override
	protected void prepareMonitoringData(List<IMonitoringRecord> records, float validationSplit) {
		List<IMonitoringRecord> copyShuffled = new ArrayList<>(records);
		Collections.shuffle(copyShuffled);

		int splitIndex = (int) Math.ceil(records.size() * validationSplit);
		int c = 0;
		for (IMonitoringRecord record : copyShuffled) {
			if (c++ < splitIndex) {
				this.trainingData.add(record);
			} else {
				this.validationData.add(record);
			}
		}

	}

}
