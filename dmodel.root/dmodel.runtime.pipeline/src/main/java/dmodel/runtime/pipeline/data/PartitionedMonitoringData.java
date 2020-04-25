package dmodel.runtime.pipeline.data;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import kieker.common.record.IMonitoringRecord;
import lombok.Getter;

@Getter
public abstract class PartitionedMonitoringData<T extends IMonitoringRecord> {

	protected List<T> trainingData;
	protected List<T> validationData;

	protected List<T> allData;

	private float validationSplit;

	public PartitionedMonitoringData(List<T> records, float validationSplit) {
		this.allData = new ArrayList<>(records);
		this.validationSplit = validationSplit;
		this.trainingData = Lists.newArrayList();
		this.validationData = Lists.newArrayList();

		prepareMonitoringData(records, validationSplit);
	}

	protected abstract void prepareMonitoringData(List<T> records, float validationSplit);

}
