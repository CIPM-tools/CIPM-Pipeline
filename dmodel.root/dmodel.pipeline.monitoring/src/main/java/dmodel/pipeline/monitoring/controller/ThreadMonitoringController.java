package dmodel.pipeline.monitoring.controller;

import java.util.Stack;

import dmodel.pipeline.monitoring.records.BranchRecord;
import dmodel.pipeline.monitoring.records.LoopRecord;
import dmodel.pipeline.monitoring.records.ResponseTimeRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import kieker.common.configuration.Configuration;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.timer.ITimeSource;
import kieker.monitoring.writer.filesystem.AsciiFileWriter;
import kieker.monitoring.writer.tcp.SingleSocketTcpWriter;

public class ThreadMonitoringController {
	private static final IMonitoringController MONITORING_CONTROLLER;
	private static final ThreadMonitoringController instance;

	static {
		final Configuration configuration = ConfigurationFactory.createDefaultConfiguration();
		configuration.setProperty(ConfigurationFactory.METADATA, "true");
		configuration.setProperty(ConfigurationFactory.AUTO_SET_LOGGINGTSTAMP, "true");
		configuration.setProperty(ConfigurationFactory.WRITER_CLASSNAME, SingleSocketTcpWriter.class.getName());
		// configuration.setProperty(WriterController.RECORD_QUEUE_SIZE, "5");
		configuration.setProperty(AsciiFileWriter.CONFIG_FLUSH, "true");
		configuration.setProperty(ConfigurationFactory.TIMER_CLASSNAME, "kieker.monitoring.timer.SystemMilliTimer");
		// configuration.setProperty(AsciiFileWriter.CONFIG_PATH, OUTPATH);

		MONITORING_CONTROLLER = MonitoringController.createInstance(configuration);
		instance = new ThreadMonitoringController();
	}

	private static final ITimeSource TIME_SOURCE = MONITORING_CONTROLLER.getTimeSource();

	private static volatile String sessionId;

	private final IDFactory idFactory;

	private ThreadLocal<Stack<ServiceCallTrack>> serviceCallStack;

	private ThreadMonitoringController() {
		this.idFactory = new IDFactory();
		this.serviceCallStack = ThreadLocal.withInitial(Stack::new);
	}

	/**
	 * Calls this method after entering the service.
	 * {@link ThreadMonitoringController#exitService()} must be called before
	 * exiting the service. Surround the inner code with try finally and call
	 * {@link ThreadMonitoringController#exitService()} inside the finally block .
	 *
	 * @param serviceId The SEFF Id for the service.
	 */
	public void enterService(final String serviceId) {
		this.enterService(serviceId, ServiceParameters.EMPTY);
	}

	/**
	 * Calls this method after entering the service.
	 * {@link ThreadMonitoringController#exitService()} must be called before
	 * exiting the service. Surround the inner code with try finally and call
	 * {@link ThreadMonitoringController#exitService()} inside the finally block .
	 *
	 * @param serviceId         The SEFF Id for the service.
	 * @param serviceParameters The service parameter values.
	 */
	public synchronized void enterService(final String serviceId, final ServiceParameters serviceParameters) {
		// value of threadlocal always exists
		Stack<ServiceCallTrack> trace = serviceCallStack.get();

		ServiceCallTrack nTrack;
		if (trace.empty()) {
			nTrack = new ServiceCallTrack(serviceId, sessionId, serviceParameters, null);
		} else {
			nTrack = new ServiceCallTrack(serviceId, sessionId, serviceParameters, trace.peek().serviceExecutionId);
		}

		// push it
		trace.push(nTrack);
	}

	/**
	 * Leaves the current service and writes the monitoring record.
	 * {@link ThreadMonitoringController#enterService(String)} must be called first.
	 */
	public synchronized void exitService() {
		long end = TIME_SOURCE.getTime();
		// value of threadlocal always exists
		Stack<ServiceCallTrack> trace = serviceCallStack.get();

		if (trace.empty()) {
			throw new RuntimeException("The trace stack should never be empty when 'exitService' is called.");
		}

		// exit current trace
		ServiceCallTrack track = trace.pop();
		MONITORING_CONTROLLER.newMonitoringRecord(new ServiceCallRecord(track.sessionId, track.serviceExecutionId, null,
				track.serviceId, track.serviceParameters.toString(), track.callerServiceExecutionId, null,
				track.serviceStartTime, end));
	}

	/**
	 * Gets the current time of the time source for monitoring records. Use this
	 * method to get the start time for response time records.
	 *
	 * @return The current time.
	 */
	public long getTime() {
		return TIME_SOURCE.getTime();
	}

	/**
	 * Writes a branch monitoring record.
	 *
	 * @param branchId         The abstract action id of the branch.
	 * @param executedBranchId The abstract action id of the executed branch
	 *                         transition.
	 */
	public void logBranchExecution(final String branchId, final String executedBranchId) {
		Stack<ServiceCallTrack> trace = serviceCallStack.get();

		if (trace.empty()) {
			throw new RuntimeException("The trace stack should never be empty when 'logBranchExecution' is called.");
		}

		ServiceCallTrack currentTrack = trace.peek();
		BranchRecord record = new BranchRecord(sessionId, currentTrack.serviceExecutionId, branchId, executedBranchId);

		MONITORING_CONTROLLER.newMonitoringRecord(record);
	}

	/**
	 * Writes a loop monitoring record.
	 *
	 * @param loopId             The abstract action id of the loop.
	 * @param loopIterationCount The executed iterations of the loop.
	 */
	public void logLoopIterationCount(final String loopId, final long loopIterationCount) {
		Stack<ServiceCallTrack> trace = serviceCallStack.get();

		if (trace.empty()) {
			throw new RuntimeException("The trace stack should never be empty when 'logLoopIterationCount' is called.");
		}

		ServiceCallTrack currentTrack = trace.peek();
		LoopRecord record = new LoopRecord(sessionId, currentTrack.serviceExecutionId, loopId, loopIterationCount);
		MONITORING_CONTROLLER.newMonitoringRecord(record);
	}

	/**
	 * Writes a response time monitoring record. The stop time of the response time
	 * is taken by this method's internals.
	 *
	 * @param internalActionId The abstract action id of the internal action.
	 * @param resourceId       The id of the resource type.
	 * @param startTime        The start time of the response time.
	 */
	public void logResponseTime(final String internalActionId, final String resourceId, final long startTime) {
		long end = TIME_SOURCE.getTime();
		Stack<ServiceCallTrack> trace = serviceCallStack.get();

		if (trace.empty()) {
			throw new RuntimeException("The trace stack should never be empty when 'logResponseTime' is called.");
		}

		ServiceCallTrack currentTrack = trace.peek();
		ResponseTimeRecord record = new ResponseTimeRecord(sessionId, currentTrack.serviceExecutionId, internalActionId,
				resourceId, startTime, end);
		MONITORING_CONTROLLER.newMonitoringRecord(record);
	}

	public static ThreadMonitoringController getInstance() {
		return instance;
	}

	/**
	 * Gets the current session id.
	 *
	 * @return The current session ids.
	 */
	public static String getSessionId() {
		return sessionId;
	}

	/**
	 * Sets the session id.
	 *
	 * @param id The new session id.
	 */
	public static void setSessionId(final String id) {
		sessionId = id;
	}

	private class ServiceCallTrack {
		private long serviceStartTime;
		private String serviceId;
		private ServiceParameters serviceParameters;
		private String serviceExecutionId;
		private String sessionId;
		private String callerServiceExecutionId;

		public ServiceCallTrack(String serviceId, String sessionId, ServiceParameters serviceParameters,
				String parentId) {
			this.serviceId = serviceId;
			this.sessionId = sessionId;
			this.serviceParameters = serviceParameters;
			this.serviceStartTime = TIME_SOURCE.getTime();
			this.callerServiceExecutionId = parentId;
			this.serviceExecutionId = ThreadMonitoringController.this.idFactory.createId();
		}

	}

}