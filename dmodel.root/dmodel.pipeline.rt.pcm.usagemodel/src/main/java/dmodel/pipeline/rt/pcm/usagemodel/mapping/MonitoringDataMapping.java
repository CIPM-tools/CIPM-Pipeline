package dmodel.pipeline.rt.pcm.usagemodel.mapping;

import java.util.HashMap;
import java.util.Map;

public class MonitoringDataMapping {

	private Map<String, String> parameterMapping;

	public MonitoringDataMapping() {
		this.parameterMapping = new HashMap<>();
	}

	public void addParameterMapping(String parameterMonitoring, String parameterSEFF) {
		this.parameterMapping.put(parameterMonitoring, parameterSEFF);
	}

	public String getSEFFParameterName(String monitoringName) {
		return this.parameterMapping.get(monitoringName);
	}

	public boolean hasSEFFParameterName(String monitoringName) {
		return this.parameterMapping.containsKey(monitoringName);
	}

	public Map<String, String> getParameterMapping() {
		return parameterMapping;
	}

	public void setParameterMapping(Map<String, String> parameterMapping) {
		this.parameterMapping = parameterMapping;
	}

}
