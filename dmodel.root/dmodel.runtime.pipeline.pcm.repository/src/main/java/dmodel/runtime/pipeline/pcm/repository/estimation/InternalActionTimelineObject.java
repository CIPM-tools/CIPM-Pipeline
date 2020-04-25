package dmodel.runtime.pipeline.pcm.repository.estimation;

import dmodel.designtime.monitoring.records.ResponseTimeRecord;

public class InternalActionTimelineObject extends AbstractTimelineObject {

	private String internalActionId;
	private String serviceId;

	public InternalActionTimelineObject(ResponseTimeRecord resp, String serviceId) {
		super(resp.getStartTime(), resp.getStopTime() - resp.getStartTime());

		this.internalActionId = resp.getInternalActionId();
		this.serviceId = serviceId;
	}

	public String getInternalActionId() {
		return internalActionId;
	}

	public void setInternalActionId(String internalActionId) {
		this.internalActionId = internalActionId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public String toString() {
		return "IA [" + internalActionId + "] - (" + this.getStart() + ", " + this.getDuration() + ")";
	}

}
