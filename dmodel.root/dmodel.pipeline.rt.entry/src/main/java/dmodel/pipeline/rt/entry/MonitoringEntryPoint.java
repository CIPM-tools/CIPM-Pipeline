package dmodel.pipeline.rt.entry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dmodel.pipeline.rt.entry.collector.IMonitoringDataCollector;
import dmodel.pipeline.rt.entry.monitoring.MultiChannelTcpReader;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import kieker.analysis.plugin.reader.util.IRecordReceivedListener;
import kieker.common.record.IMonitoringRecord;

@Service
@lombok.extern.java.Log
public class MonitoringEntryPoint implements InitializingBean, IRecordReceivedListener {
	@Autowired
	private List<IMonitoringDataCollector> collectors;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	private MultiChannelTcpReader tcpReader;
	private ExecutorService clientHandlingExecutorService;

	public MonitoringEntryPoint() {
		clientHandlingExecutorService = Executors.newFixedThreadPool(10); // max 10 concurrent
		tcpReader = new MultiChannelTcpReader(10133, 65535, this, clientHandlingExecutorService);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		new Thread(new ReRunRunnableWrapper(tcpReader)).start();
	}

	@Override
	public synchronized void onRecordReceived(IMonitoringRecord record) {
		// tell it the blackboard
		blackboard.receivedMonitoringData();
		blackboard.setApplicationRunning(true);

		log.finer("Received a monitoring record of type \"" + record.getClass().getName() + "\".");
		for (IMonitoringDataCollector collector : collectors) {
			collector.collect(record);
		}
	}

	private class ReRunRunnableWrapper implements Runnable {
		private Runnable run;

		private ReRunRunnableWrapper(Runnable run) {
			this.run = run;
		}

		@Override
		public void run() {
			while (true) {
				try {
					run.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
