package dmodel.pipeline.rt.entry;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dmodel.pipeline.rt.entry.collector.IMonitoringDataCollector;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import kieker.analysis.plugin.reader.tcp.util.SingleSocketRecordReader;
import kieker.analysis.plugin.reader.util.IRecordReceivedListener;
import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.common.record.IMonitoringRecord;

@Service
public class MonitoringEntryPoint implements InitializingBean, IRecordReceivedListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringEntryPoint.class);
	private static final Log KIEKER_LOGGER = LogFactory.getLog(MonitoringEntryPoint.class);

	private SingleSocketRecordReader monitoringReader;

	@Autowired
	private List<IMonitoringDataCollector> collectors;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	public MonitoringEntryPoint() {
		monitoringReader = new SingleSocketRecordReader(10133, 65535, KIEKER_LOGGER, this);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		new Thread(new ReRunRunnableWrapper(monitoringReader)).start();
	}

	@Override
	public void onRecordReceived(IMonitoringRecord record) {
		// tell it the blackboard
		blackboard.receivedMonitoringData();
		blackboard.setApplicationRunning(true);

		LOGGER.debug("Received a monitoring record of type \"" + record.getClass().getName() + "\".");
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
