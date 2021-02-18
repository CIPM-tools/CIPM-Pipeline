package cipm.consistency.runtime.pipeline.pcm.repository.calibration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ResponseTimeRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.bridge.monitoring.util.ServiceParametersWrapper;
import lombok.Data;

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
					if (nMap.get(rtr.getInternalActionId()).addTuple(rtr.getServiceExecutionId(),
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

	@Data
	public static class RegressionDataset {
		private List<Pair<Map<String, Double>, Double>> records;
		private String actionId;
		private Set<String> containedExecutionIds;

		private RegressionDataset(String actionId) {
			this.records = Lists.newArrayList();
			this.actionId = actionId;
			this.containedExecutionIds = Sets.newHashSet();
		}

		private boolean addTuple(String serviceExecutionId, Map<String, Double> parameters, double executionTime) {
			if (!containedExecutionIds.contains(serviceExecutionId)) {
				this.containedExecutionIds.add(serviceExecutionId);
				this.records.add(Pair.of(parameters, executionTime));
				return true;
			}
			return false;
		}
	}

}
