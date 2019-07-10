package dmodel.pipeline.rt.pcm.usagemodel.data;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pcm.usagemodel.util.ServiceCallUtil;

public class ServiceCallBundle {
	private ServiceCallRecord serviceCall;
	private int callCount;

	public ServiceCallBundle(ServiceCallRecord call) {
		this.callCount = 1;
		this.serviceCall = call;
	}

	public boolean isLoop() {
		return this.callCount > 1;
	}

	public boolean canBeBundled(ServiceCallBundle bundle) {
		return this.serviceCall.getServiceId().equals(bundle.getServiceCall().getServiceId());
	}

	public boolean canBeBundled(ServiceCallRecord id) {
		return this.serviceCall.getServiceId().equals(id.getServiceId());
	}

	public void bundle(ServiceCallRecord other) {
		this.serviceCall = ServiceCallUtil.merge(this.serviceCall, other);
		this.callCount++;
	}

	public int getCallCount() {
		return this.callCount;
	}

	public ServiceCallRecord getServiceCall() {
		return this.serviceCall;
	}

}
