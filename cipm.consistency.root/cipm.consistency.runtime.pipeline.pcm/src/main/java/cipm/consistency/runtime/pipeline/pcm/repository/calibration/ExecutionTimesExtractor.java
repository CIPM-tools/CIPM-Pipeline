package cipm.consistency.runtime.pipeline.pcm.repository.calibration;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Sets;

import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ResponseTimeRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.bridge.monitoring.util.ServiceParametersWrapper;
import lombok.Getter;

// TODO code duplication
public class ExecutionTimesExtractor {
	private static final double NANO_TO_MS = 1000000;

	public List<RegressionDataset> mergeDatasets(Map<String, RegressionDataset> existing, List<PCMContextRecord> data) {
		// get parameters
		Map<String, Map<String, Double>> serviceExecutionParameterMapping = Maps.newHashMap();
		for (PCMContextRecord rec : data) {
			if (rec instanceof ServiceCallRecord) {
				ServiceCallRecord sc = (ServiceCallRecord) rec;
				Map<String, Object> parameters = ServiceParametersWrapper.buildFromJson(sc.getParameters())
						.getParameters();
				Map<String, Double> numberParameters = Maps.newHashMap();
				parameters.entrySet().forEach(e -> {
					if (e.getValue() instanceof Number) {
						numberParameters.put(e.getKey(), ((Number) e.getValue()).doubleValue());
					}
				});
				serviceExecutionParameterMapping.put(sc.getServiceExecutionId(), numberParameters);
			}
		}

		// copy map
		Map<String, RegressionDataset> nMap = Maps.newHashMap();
		Set<String> changedOnes = Sets.newHashSet();
		for (PCMContextRecord rec : data) {
			if (rec instanceof ResponseTimeRecord) {
				ResponseTimeRecord rtr = (ResponseTimeRecord) rec;
				if (serviceExecutionParameterMapping.containsKey(rtr.getServiceExecutionId())) {
					if (!nMap.containsKey(rtr.getInternalActionId())) {
						if (existing.containsKey(rtr.getInternalActionId())) {
							nMap.put(rtr.getInternalActionId(), existing.get(rtr.getInternalActionId()));
						} else {
							nMap.put(rtr.getInternalActionId(), new RegressionDataset(rtr.getInternalActionId()));
						}
					}
					if (nMap.get(rtr.getInternalActionId()).addTuple(rtr.getStartTime(), rtr.getServiceExecutionId(),
							serviceExecutionParameterMapping.get(rtr.getServiceExecutionId()),
							((double) rtr.getStopTime() - rtr.getStartTime()) / NANO_TO_MS)) {
						changedOnes.add(rtr.getInternalActionId());
					}
				}
			}
		}

		return nMap.entrySet().stream().filter(k -> changedOnes.contains(k.getKey())).map(e -> e.getValue())
				.collect(Collectors.toList());
	}

	public static class RegressionDataset {
		private SortedMap<Long, Pair<Map<String, Double>, Double>> records;
		@Getter
		private String actionId;
		private Set<String> containedExecutionIds;

		private RegressionDataset(String actionId) {
			this.records = new TreeMap<>();
			this.actionId = actionId;
			this.containedExecutionIds = Sets.newHashSet();
		}

		private boolean addTuple(long timestamp, String serviceExecutionId, Map<String, Double> parameters,
				double executionTime) {
			if (!containedExecutionIds.contains(serviceExecutionId)) {
				this.containedExecutionIds.add(serviceExecutionId);
				this.records.put((long) (timestamp / NANO_TO_MS), Pair.of(parameters, executionTime));
				return true;
			}
			return false;
		}

		public void filterRecords(Predicate<? super Entry<Long, Pair<Map<String, Double>, Double>>> predicate) {
			this.records.entrySet().stream().filter(predicate) // small change
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, TreeMap::new));
		}

		public List<Pair<Map<String, Double>, Double>> getRecords() {
			return this.records.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
		}

		public void cutData(long windowSizeSeconds) {
			long currentTime = System.currentTimeMillis();
			this.records.headMap(currentTime - windowSizeSeconds * 1000).clear();
		}
	}

}
