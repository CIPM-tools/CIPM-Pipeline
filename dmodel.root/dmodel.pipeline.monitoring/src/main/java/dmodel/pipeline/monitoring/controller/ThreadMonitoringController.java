package dmodel.pipeline.monitoring.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.monitoring.records.BranchRecord;
import dmodel.pipeline.monitoring.records.LoopRecord;
import dmodel.pipeline.monitoring.records.ResponseTimeRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import kieker.common.configuration.Configuration;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.sampler.ScheduledSamplerJob;
import kieker.monitoring.timer.ITimeSource;
import kieker.monitoring.writer.tcp.SingleSocketTcpWriter;

// this should be java 7 compatible to be useable with all kinds of applications
public class ThreadMonitoringController {
	private static IMonitoringController MONITORING_CONTROLLER;
	private static final ThreadMonitoringController instance;

	private static final String serverHostname = "localhost";
	private static final int restPort = 8090;
	private static final String restAddress = "/runtime/pipeline/imm";

	static {
		createMonitoringController();
		instance = new ThreadMonitoringController();
		instance.pollInstrumentationModel();
		instance.registerInstrumentationModelPoll();
		instance.registerCpuSampler();
	}

	private static final ITimeSource TIME_SOURCE = MONITORING_CONTROLLER.getTimeSource();

	private static volatile String sessionId;

	private final IDFactory idFactory;

	private ThreadLocal<Stack<ServiceCallTrack>> serviceCallStack;
	private ThreadLocal<InternalOptional<String>> remoteStack;

	private Set<String> monitoredIds = new HashSet<>();
	private boolean monitoredIdsInited = false;
	private ObjectMapper objectMapper = new ObjectMapper();

	private ScheduledExecutorService execService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
		public Thread newThread(Runnable r) {
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			return t;
		}
	});
	private MonitoringAnalysisData analysis = new MonitoringAnalysisData();
	private boolean cpuSamplerActive;
	private ScheduledSamplerJob samplerJob;

	private ThreadMonitoringController() {
		this.idFactory = new IDFactory();
		this.serviceCallStack = ThreadLocal.withInitial(new Supplier<Stack<ServiceCallTrack>>() {
			@Override
			public Stack<ServiceCallTrack> get() {
				return new Stack<ServiceCallTrack>();
			}
		});
		this.remoteStack = ThreadLocal.withInitial(new Supplier<InternalOptional<String>>() {
			@Override
			public InternalOptional<String> get() {
				return new InternalOptional<String>();
			}
		});
		this.cpuSamplerActive = false;
	}

	private static void createMonitoringController() {
		final Configuration configuration = ConfigurationFactory.createDefaultConfiguration();
		configuration.setProperty(ConfigurationFactory.METADATA, "true");
		configuration.setProperty(ConfigurationFactory.AUTO_SET_LOGGINGTSTAMP, "true");
		configuration.setProperty(ConfigurationFactory.WRITER_CLASSNAME, SingleSocketTcpWriter.class.getName());
		// configuration.setProperty(WriterController.RECORD_QUEUE_SIZE, "5");
		configuration.setProperty(SingleSocketTcpWriter.CONFIG_FLUSH, "true");
		configuration.setProperty(ConfigurationFactory.TIMER_CLASSNAME, "kieker.monitoring.timer.SystemMilliTimer");
		configuration.setProperty(SingleSocketTcpWriter.CONFIG_HOSTNAME, "localhost");
		// configuration.setProperty(AsciiFileWriter.CONFIG_PATH, OUTPATH);

		MONITORING_CONTROLLER = MonitoringController.createInstance(configuration);
	}

	public void registerCpuSampler() {
		if (!cpuSamplerActive) {
			CPUSamplingJob job = new CPUSamplingJob();

			samplerJob = MONITORING_CONTROLLER.schedulePeriodicSampler(job, 0, 100, TimeUnit.MILLISECONDS);
			cpuSamplerActive = true;
		}
	}

	public void unregisterCpuSampler() {
		if (cpuSamplerActive) {
			MONITORING_CONTROLLER.removeScheduledSampler(samplerJob);
			cpuSamplerActive = false;
		}
	}

	private void registerInstrumentationModelPoll() {
		execService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				pollInstrumentationModel();
				analysis.writeOverhead(); // write the overhead to a file

				if (MONITORING_CONTROLLER.isMonitoringTerminated()) {
					// retry
					createMonitoringController();
				}
			}
		}, 15, 15, TimeUnit.SECONDS);
	}

	private void pollInstrumentationModel() {
		try {
			final URL url = new URL("http://" + serverHostname + ":" + restPort + restAddress);
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			final BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			String all = null;
			while ((output = br.readLine()) != null) {
				all = all == null ? output : all + "\n" + output;
			}
			conn.disconnect();

			monitoredIds = new HashSet<>(Arrays.asList(objectMapper.readValue(all, String[].class)));
			monitoredIdsInited = true;
		} catch (IOException e) {
			monitoredIdsInited = false;
		}

	}

	public synchronized void continueFromRemote(final String callerId) {
		this.remoteStack.get().set(callerId);
	}

	public synchronized void detachFromRemote() {
		this.remoteStack.get().clear();
	}

	/**
	 * Calls this method after entering the service.
	 * {@link ThreadMonitoringController#exitService()} must be called before
	 * exiting the service. Surround the inner code with try finally and call
	 * {@link ThreadMonitoringController#exitService()} inside the finally block .
	 *
	 * @param serviceId The SEFF Id for the service.
	 */
	public void enterService(final String serviceId, final Object exId) {
		this.enterService(serviceId, exId, ServiceParameters.EMPTY);
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
	public synchronized void enterService(final String serviceId, final Object exId,
			final ServiceParameters serviceParameters) {
		if (!monitoredIdsInited || monitoredIds.contains(serviceId)) {
			long start = analysis.enterOverhead();

			// value of threadlocal always exists
			Stack<ServiceCallTrack> trace = serviceCallStack.get();

			ServiceCallTrack nTrack;
			if (trace.empty()) {
				if (remoteStack.get().isPresent()) {
					nTrack = new ServiceCallTrack(serviceId, sessionId, serviceParameters,
							String.valueOf(System.identityHashCode(exId)), remoteStack.get().value);
				} else {
					nTrack = new ServiceCallTrack(serviceId, sessionId, serviceParameters,
							String.valueOf(System.identityHashCode(exId)), null);
				}
			} else {
				nTrack = new ServiceCallTrack(serviceId, sessionId, serviceParameters,
						String.valueOf(System.identityHashCode(exId)), trace.peek().serviceExecutionId);
			}

			// push it
			trace.push(nTrack);

			analysis.exitServiceCallOverhead(serviceId, start);
		}
	}

	/**
	 * Leaves the current service and writes the monitoring record.
	 * {@link ThreadMonitoringController#enterService(String)} must be called first.
	 */
	public synchronized void exitService(String serviceId) {
		if (!monitoredIdsInited || monitoredIds.contains(serviceId)) {
			long start = analysis.enterOverhead();

			long end = TIME_SOURCE.getTime();
			// value of threadlocal always exists
			Stack<ServiceCallTrack> trace = serviceCallStack.get();

			if (trace.empty()) {
				throw new RuntimeException("The trace stack should never be empty when 'exitService' is called.");
			}

			// exit current trace
			ServiceCallTrack track = trace.pop();
			MONITORING_CONTROLLER.newMonitoringRecord(new ServiceCallRecord(track.sessionId, track.serviceExecutionId,
					HostNameFactory.generateHostId(), HostNameFactory.generateHostName(), track.serviceId,
					track.serviceParameters.toString(), track.callerServiceExecutionId, track.executionContext,
					track.serviceStartTime, end - track.cumulatedMonitoringOverhead));

			if (!trace.isEmpty()) {
				ServiceCallTrack parent = trace.peek();
				parent.cumulatedMonitoringOverhead += track.cumulatedMonitoringOverhead;
			}

			analysis.exitServiceCallOverhead(serviceId, start);
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
		if (!monitoredIdsInited || monitoredIds.contains(branchId)) {
			long start = analysis.enterOverhead();

			Stack<ServiceCallTrack> trace = serviceCallStack.get();

			if (trace.empty()) {
				throw new RuntimeException(
						"The trace stack should never be empty when 'logBranchExecution' is called.");
			}

			ServiceCallTrack currentTrack = trace.peek();
			BranchRecord record = new BranchRecord(sessionId, currentTrack.serviceExecutionId, branchId,
					executedBranchId);

			MONITORING_CONTROLLER.newMonitoringRecord(record);

			analysis.exitBranchOverhead(branchId, start);
			currentTrack.cumulatedMonitoringOverhead += (System.nanoTime() - start);
		}
	}

	/**
	 * Writes a loop monitoring record.
	 *
	 * @param loopId             The abstract action id of the loop.
	 * @param loopIterationCount The executed iterations of the loop.
	 */
	public void logLoopIterationCount(final String loopId, final long loopIterationCount) {
		if (!monitoredIdsInited || monitoredIds.contains(loopId)) {
			long start = analysis.enterOverhead();
			Stack<ServiceCallTrack> trace = serviceCallStack.get();

			if (trace.empty()) {
				throw new RuntimeException(
						"The trace stack should never be empty when 'logLoopIterationCount' is called.");
			}

			ServiceCallTrack currentTrack = trace.peek();
			LoopRecord record = new LoopRecord(sessionId, currentTrack.serviceExecutionId, loopId, loopIterationCount);
			MONITORING_CONTROLLER.newMonitoringRecord(record);

			analysis.exitLoopOverhead(loopId, start);
			currentTrack.cumulatedMonitoringOverhead += (System.nanoTime() - start);
		}
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
		if (!monitoredIdsInited || monitoredIds.contains(internalActionId)) {
			long start = analysis.enterOverhead();

			long end = TIME_SOURCE.getTime();
			Stack<ServiceCallTrack> trace = serviceCallStack.get();

			if (trace.empty()) {
				throw new RuntimeException("The trace stack should never be empty when 'logResponseTime' is called.");
			}

			ServiceCallTrack currentTrack = trace.peek();
			ResponseTimeRecord record = new ResponseTimeRecord(sessionId, currentTrack.serviceExecutionId,
					internalActionId, resourceId, startTime, end);
			MONITORING_CONTROLLER.newMonitoringRecord(record);

			analysis.internalOverhead(internalActionId, start);
			currentTrack.cumulatedMonitoringOverhead += (System.nanoTime() - start);
		}
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
		private String executionContext;

		private long cumulatedMonitoringOverhead;

		public ServiceCallTrack(String serviceId, String sessionId, ServiceParameters serviceParameters,
				String executionContext, String parentId) {
			this.serviceId = serviceId;
			this.sessionId = sessionId;
			this.serviceParameters = serviceParameters;
			this.serviceStartTime = TIME_SOURCE.getTime();
			this.callerServiceExecutionId = parentId;
			this.executionContext = executionContext;
			this.serviceExecutionId = ThreadMonitoringController.this.idFactory.createId();
			this.cumulatedMonitoringOverhead = 0;
		}

	}

	// DUE TO COMPATIBILITY REASONS
	// OPTIONAL IS AVAILABLE AT JAVA >= 8
	private static class InternalOptional<T> {
		private T value;
		private boolean present;

		private InternalOptional() {
			this.present = false;
		}

		private InternalOptional(T value) {
			this.set(value);
		}

		private boolean isPresent() {
			return present;
		}

		private void set(T value) {
			this.value = value;
			this.present = true;
		}

		private void clear() {
			this.present = false;
			this.value = null;
		}
	}

}