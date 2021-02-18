package cipm.consistency.base.evaluation.overhead;

import java.util.Map;

import lombok.Data;

@Data
public class MonitoringOverheadData {
	private Map<String, Long> nanoOverheadMap;
	private long timestamp;
	private long endTimestamp;
}