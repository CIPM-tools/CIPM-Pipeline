package dmodel.pipeline.monitoring.controller;

import org.hyperic.sigar.Sigar;

import dmodel.pipeline.monitoring.records.ResourceUtilizationRecord;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.sampler.ISampler;
import kieker.monitoring.timer.ITimeSource;

public class CPUSamplingJob implements ISampler {

	private static final ITimeSource TIME_SOURCE = MonitoringController.getInstance().getTimeSource();

	private String hostId;
	private String hostName;

	private Sigar sigar;

	public CPUSamplingJob(String hostName, String hostId) {
		this.hostName = hostName;
		this.hostId = hostId;

		this.sigar = new Sigar();
	}

	public CPUSamplingJob() {
		this(HostNameFactory.generateHostName(), HostNameFactory.generateHostId());
	}

	@Override
	public void sample(IMonitoringController arg0) throws Exception {
		double cpuPerc = sigar.getCpuPerc().getCombined();

		ResourceUtilizationRecord util = new ResourceUtilizationRecord(hostId, hostName,
				MonitoringMetadata.RESOURCE_CPU, cpuPerc, TIME_SOURCE.getTime());
		arg0.newMonitoringRecord(util);
	}

}
