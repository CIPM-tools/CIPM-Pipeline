package cipm.consistency.runtime.pipeline.pcm.usagemodel;

import java.util.List;

import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;

public class ServiceCallSession {
	private String sessionId;
	private List<ServiceCallRecord> entryCalls;

	public ServiceCallSession(String sessionId, List<ServiceCallRecord> entryCalls) {
		this.sessionId = sessionId;
		this.entryCalls = entryCalls;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<ServiceCallRecord> getEntryCalls() {
		return entryCalls;
	}

	public void setEntryCalls(List<ServiceCallRecord> entryCalls) {
		this.entryCalls = entryCalls;
	}

}
