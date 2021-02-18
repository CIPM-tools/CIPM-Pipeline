package cipm.consistency.runtime.pipeline.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cipm.consistency.base.shared.structure.Tree.TreeNode;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.bridge.monitoring.records.ServiceContextRecord;
import cipm.consistency.bridge.monitoring.util.MonitoringDataUtil;

public class PCMPartionedMonitoringData extends PartitionedMonitoringData<PCMContextRecord> {

	public PCMPartionedMonitoringData(List<PCMContextRecord> records, float validationSplit) {
		super(records, validationSplit);
	}

	@Override
	protected void prepareMonitoringData(List<PCMContextRecord> records, float validationSplit) {
		// 1. map records to root execution IDs
		final Map<String, List<List<String>>> rootExecutionIdMapping = mapRootExecutionIds(records);

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
		List<PCMContextRecord> shuffleCopy = Lists.newArrayList(records);
		Collections.shuffle(shuffleCopy);

		for (PCMContextRecord rec : shuffleCopy) {
			if (rec instanceof ServiceContextRecord) {
				String contextId = ((ServiceContextRecord) rec).getServiceExecutionId();

				if (trainingContextIds.contains(contextId)) {
					this.trainingData.add(rec);
				} else {
					this.validationData.add(rec);
				}
			} else {
				this.trainingData.add(rec);
				this.validationData.add(rec);
			}
		}
	}

	private void generateMonitoringSets(List<PCMContextRecord> records,
			Map<String, List<List<String>>> rootExecutionIdMapping, float validationSplit,
			Set<String> trainingContextIds, Set<String> validationContextIds) {
		rootExecutionIdMapping.keySet().forEach(key -> {
			List<List<String>> list = rootExecutionIdMapping.get(key);
			int keySplit = (int) Math.ceil(list.size() * validationSplit);

			// shuffle
			Collections.shuffle(list);

			// iterate
			int counter = 0;
			for (List<String> value : list) {
				if (counter++ > keySplit) {
					trainingContextIds.addAll(value);
				} else {
					validationContextIds.addAll(value);
				}
			}
		});
	}

	private Map<String, List<List<String>>> mapRootExecutionIds(List<PCMContextRecord> records) {
		final Map<String, List<List<String>>> rootExecutionIdMapping = Maps.newHashMap();

		MonitoringDataUtil.buildServiceCallTree(records.stream().filter(f -> f instanceof ServiceCallRecord)
				.map(ServiceCallRecord.class::cast).collect(Collectors.toList())).forEach(t -> {
					List<String> resIds = Lists.newArrayList(collectSubIds(t.getRoot()));
					String correspondingServiceId = t.getRoot().getData().getServiceId();

					// put
					if (rootExecutionIdMapping.containsKey(correspondingServiceId)) {
						rootExecutionIdMapping.get(correspondingServiceId).add(resIds);
					} else {
						List<List<String>> res = new ArrayList<>();
						res.add(resIds);
						rootExecutionIdMapping.put(correspondingServiceId, res);
					}
				});

		return rootExecutionIdMapping;
	}

	private Collection<? extends String> collectSubIds(TreeNode<ServiceCallRecord> root) {
		List<String> res = Lists.newArrayList();
		res.add(root.getData().getServiceExecutionId());
		root.getChildren().forEach(n -> {
			res.addAll(collectSubIds(n));
		});
		return res;
	}

}
