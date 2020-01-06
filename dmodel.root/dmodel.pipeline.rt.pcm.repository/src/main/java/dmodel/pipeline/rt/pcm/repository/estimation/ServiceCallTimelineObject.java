package dmodel.pipeline.rt.pcm.repository.estimation;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.ServiceParametersWrapper;

public class ServiceCallTimelineObject extends AbstractTimelineObject {

	private String serviceId;
	private ServiceParametersWrapper parameters;

	public ServiceCallTimelineObject(ServiceCallRecord call) {
		super(call.getEntryTime(), call.getExitTime() - call.getEntryTime());

		this.serviceId = call.getServiceId();
		this.parameters = ServiceParametersWrapper.buildFromJson(call.getParameters());
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public ServiceParametersWrapper getParameters() {
		return parameters;
	}

	public void setParameters(ServiceParametersWrapper parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return "SC [" + serviceId + "] - (" + this.getStart() + ", " + this.getDuration() + ")";
	}

}
