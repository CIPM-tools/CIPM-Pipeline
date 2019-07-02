package dmodel.pipeline.monitoring.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

	// depend on the used TimeUnit
	private static final long VALID_MINIMAL_RESPONSE_TIME = 100;

	private static final int INITIAL_SERVICE_DEPTH_COUNT = 10;

	private static final IMonitoringController MONITORING_CONTROLLER;

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
	}

	private static final ITimeSource TIME_SOURCE = MONITORING_CONTROLLER.getTimeSource();

	private static final ThreadLocal<ThreadMonitoringController> CONTROLLER = ThreadLocal.withInitial(
			() -> new ThreadMonitoringController(Thread.currentThread().getId(), INITIAL_SERVICE_DEPTH_COUNT));

	private static volatile String sessionId;

	private final long threadId;

	/**
	 * Stack and cache of service monitoring controllers. Already initialized
	 * controllers are reused.
	 */
	private final List<ServiceMonitoringController> serviceControllers;

	private int currentServiceIndex;

	private ServiceMonitoringController currentServiceController;

	private ThreadMonitoringController(final long threadId, final int initialServiceDepthCount) {
		this.threadId = threadId;
		this.serviceControllers = new ArrayList<>(initialServiceDepthCount);
		for (int i = 0; i < initialServiceDepthCount; i++) {
			this.serviceControllers.add(new ServiceMonitoringController());
		}
		this.currentServiceIndex = -1;
		this.currentServiceController = null;
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
	public void enterService(final String serviceId, final ServiceParameters serviceParameters) {
		String currentServiceExecutionId = null;
		String currentCallerId = null;
		if (this.currentServiceController != null) {
			currentServiceExecutionId = this.currentServiceController.getServiceExecutionId();
			currentCallerId = this.currentServiceController.getCurrentCallerId();
		}

		this.currentServiceIndex += 1;
		ServiceMonitoringController newService;
		if (this.currentServiceIndex >= this.serviceControllers.size()) {
			newService = new ServiceMonitoringController();
			this.serviceControllers.add(new ServiceMonitoringController());
		} else {
			newService = this.serviceControllers.get(this.currentServiceIndex);
		}

		newService.enterService(serviceId, this.threadId, sessionId, serviceParameters, currentCallerId,
				currentServiceExecutionId);

		this.currentServiceController = newService;
	}

	/**
	 * Leaves the current service and writes the monitoring record.
	 * {@link ThreadMonitoringController#enterService(String)} must be called first.
	 */
	public void exitService() {
		this.currentServiceController.exitService();
		this.currentServiceIndex -= 1;
		if (this.currentServiceIndex >= 0) {
			this.currentServiceController = this.serviceControllers.get(this.currentServiceIndex);
		} else {
			this.currentServiceController = null;
		}
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
		this.currentServiceController.logBranchExecution(branchId, executedBranchId);
	}

	/**
	 * Writes a loop monitoring record.
	 *
	 * @param loopId             The abstract action id of the loop.
	 * @param loopIterationCount The executed iterations of the loop.
	 */
	public void logLoopIterationCount(final String loopId, final long loopIterationCount) {
		this.currentServiceController.logLoopIterationCount(loopId, loopIterationCount);
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
		this.currentServiceController.logResponseTime(internalActionId, resourceId, startTime);
	}

	/**
	 * Sets the abstract action id of the next external call.
	 *
	 * @param currentCallerId The abstract action id of the next external call.
	 */
	public void setCurrentCallerId(final String currentCallerId) {
		this.currentServiceController.setCurrentCallerId(currentCallerId);
	}

	/**
	 * Gets the singleton instance. Each thread has its own instance.
	 *
	 * @return Instance of {@link ThreadMonitoringController}.
	 */
	public static ThreadMonitoringController getInstance() {
		return CONTROLLER.get();
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

	private static class ServiceMonitoringController {

		private long serviceStartTime;

		private String serviceId;
		private ServiceParameters serviceParameters;
		private String serviceExecutionId;
		private String sessionId;
		private String callerServiceExecutionId;
		private String callerId;
		private String currentCallerId;

		public void enterService(final String serviceId, final long threadId, final String sessionId,
				final ServiceParameters serviceParameters, final String callerId,
				final String callerServiceExecutionId) {
			this.serviceId = serviceId;
			this.sessionId = sessionId;
			this.serviceParameters = serviceParameters;
			this.callerServiceExecutionId = callerServiceExecutionId;
			this.callerId = callerId;
			this.serviceStartTime = TIME_SOURCE.getTime();
			this.serviceExecutionId = UUID.randomUUID().toString();
			this.currentCallerId = null;
		}

		public void exitService() {
			final long stopTime = TIME_SOURCE.getTime();

			ServiceCallRecord e = new ServiceCallRecord(this.sessionId, this.serviceExecutionId, null, this.serviceId,
					this.serviceParameters.toString(), this.callerServiceExecutionId, this.callerId, null,
					this.serviceStartTime, stopTime);
			MONITORING_CONTROLLER.newMonitoringRecord(e);
		}

		public String getCurrentCallerId() {
			return this.currentCallerId;
		}

		public String getServiceExecutionId() {
			return this.serviceExecutionId;
		}

		public void logBranchExecution(final String branchId, final String executedBranchId) {
			BranchRecord record = new BranchRecord(this.sessionId, this.serviceExecutionId, branchId, executedBranchId);
			MONITORING_CONTROLLER.newMonitoringRecord(record);
		}

		public void logLoopIterationCount(final String loopId, final long loopIterationCount) {
			LoopRecord record = new LoopRecord(this.sessionId, this.serviceExecutionId, loopId, loopIterationCount);
			MONITORING_CONTROLLER.newMonitoringRecord(record);
		}

		public void logResponseTime(final String internalActionId, final String resourceId, final long startTime) {
			long currentTime = TIME_SOURCE.getTime();

			ResponseTimeRecord record = new ResponseTimeRecord(this.sessionId, this.serviceExecutionId,
					internalActionId, resourceId, startTime, currentTime);

			MONITORING_CONTROLLER.newMonitoringRecord(record);

		}

		public void setCurrentCallerId(final String currentCallerId) {
			this.currentCallerId = currentCallerId;
		}

		private boolean isResponseTimeValid(long statTime, long endTime) {
			// convert the duration in milliseconds
			long duration = 0;

			if (TIME_SOURCE.getTimeUnit().equals(TimeUnit.NANOSECONDS)) {
				duration = TimeUnit.MILLISECONDS.convert(endTime - statTime, TimeUnit.NANOSECONDS);
			} else if (TIME_SOURCE.getTimeUnit().equals(TimeUnit.MILLISECONDS)) {
				duration = endTime - statTime;
			}

			if (duration < VALID_MINIMAL_RESPONSE_TIME) {
				return false;
			}

			return true;
		}

	}

}