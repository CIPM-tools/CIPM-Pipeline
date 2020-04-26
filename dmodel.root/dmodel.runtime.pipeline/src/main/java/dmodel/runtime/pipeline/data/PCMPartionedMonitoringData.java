package dmodel.runtime.pipeline.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.designtime.monitoring.records.ServiceContextRecord;
import dmodel.designtime.monitoring.util.MonitoringDataUtil;

public class PCMPartionedMonitoringData extends PartitionedMonitoringData<PCMContextRecord> {

	public PCMPartionedMonitoringData(List<PCMContextRecord> records, float validationSplit) {
		super(records, validationSplit);
	}

	@Override
	protected void prepareMonitoringData(List<PCMContextRecord> records, float validationSplit) {
		// 1. map records to root execution IDs
		final Map<String, List<String>> rootExecutionIdMapping = mapRootExecutionIds(records);

		// 2. generate sets
		Set<String> trainingContextIds = Sets.newHashSet();
		Set<String> validationContextIds = Sets.newHashSet();

		generateMonitoringSets(records, rootExecutionIdMapping, validationSplit, trainingContextIds,
				validationContextIds);

		// 3. generate data
		generateData(records, trainingContextIds, validationContextIds, validationSplit);
	}

	private void generateData(List<PCMContextRecord> records, Set<String> trainingContextIds,
			Set<String> validationContextIds, float validationSplit) {
		int otherSplit = (int) Math
				.ceil((records.size() - (trainingContextIds.size() + validationContextIds.size())) * validationSplit);
		int otherCounter = 0;

		for (PCMContextRecord rec : records) {
			if (rec instanceof ServiceContextRecord) {
				String contextId = ((ServiceContextRecord) rec).getServiceExecutionId();
				if (trainingContextIds.contains(contextId)) {
					this.trainingData.add(rec);
				} else {
					this.validationData.add(rec);
				}
			} else {
				if (otherCounter++ > otherSplit) {
					this.trainingData.add(rec);
				} else {
					this.validationData.add(rec);
				}
			}
		}
	}

	private void generateMonitoringSets(List<PCMContextRecord> records,
			Map<String, List<String>> rootExecutionIdMapping, float validationSplit, Set<String> trainingContextIds,
			Set<String> validationContextIds) {
		rootExecutionIdMapping.keySet().forEach(key -> {
			List<String> list = rootExecutionIdMapping.get(key);
			int keySplit = (int) Math.ceil(list.size() * validationSplit);

			// shuffle
			List<PCMContextRecord> shuffleCopy = new ArrayList<>(records);
			Collections.shuffle(shuffleCopy);

			// iterate
			int counter = 0;
			for (String value : list) {
				if (counter++ > keySplit) {
					trainingContextIds.add(value);
				} else {
					validationContextIds.add(value);
				}
			}
		});
	}

	private Map<String, List<String>> mapRootExecutionIds(List<PCMContextRecord> records) {
		final Map<String, List<String>> rootExecutionIdMapping = Maps.newHashMap();

		MonitoringDataUtil.buildServiceCallTree(records.stream().filter(f -> f instanceof ServiceCallRecord)
				.map(ServiceCallRecord.class::cast).collect(Collectors.toList())).forEach(t -> {
					String correspondingServiceId = t.getRoot().getData().getServiceId();
					if (rootExecutionIdMapping.containsKey(correspondingServiceId)) {
						rootExecutionIdMapping.get(correspondingServiceId)
								.add(t.getRoot().getData().getServiceExecutionId());
					} else {
						rootExecutionIdMapping.put(correspondingServiceId,
								Lists.newArrayList(t.getRoot().getData().getServiceExecutionId()));
					}
				});

		return rootExecutionIdMapping;
	}

}
