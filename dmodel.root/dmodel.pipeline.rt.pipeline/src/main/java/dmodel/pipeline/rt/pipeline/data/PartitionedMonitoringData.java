package dmodel.pipeline.rt.pipeline.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import kieker.common.record.IMonitoringRecord;
import lombok.Getter;

@Getter
public class PartitionedMonitoringData<T extends IMonitoringRecord> {

	private List<T> trainingData;
	private List<T> validationData;

	private List<T> allData;

	private float validationSplit;

	public PartitionedMonitoringData(List<T> records, float validationSplit) {
		this.allData = new ArrayList<>(records);
		this.validationSplit = validationSplit;

		prepareMonitoringData(records, validationSplit);
	}

	private void prepareMonitoringData(List<T> records, float validationSplit) {
		List<T> copyShuffled = new ArrayList<>(records);
		Collections.shuffle(copyShuffled);

		this.trainingData = Lists.newArrayList();
		this.validationData = Lists.newArrayList();

		int splitIndex = (int) Math.ceil(records.size() * validationSplit);
		int c = 0;
		for (T record : copyShuffled) {
			if (c < splitIndex) {
				this.trainingData.add(record);
			} else {
				this.validationData.add(record);
			}
		}
	}

}
