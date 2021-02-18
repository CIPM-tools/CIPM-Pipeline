package cipm.consistency.runtime.pipeline.entry;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.runtime.pipeline.entry.MonitoringEntryPoint;
import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.writer.tcp.SingleSocketTcpWriter;
import lombok.extern.java.Log;

@Log
public class MultipleClientTest {

	private ModifiedMonitoringEntryPoint entryPoint;

	@Before
	public void setUp() throws Exception {
		entryPoint = new ModifiedMonitoringEntryPoint();
		entryPoint.start();
	}

	@Test
	public void test() {
		int amount = 5;
		CountDownLatch waiter = new CountDownLatch(amount);

		for (int k = 0; k < amount; k++) {
			Sender nSender = new Sender();
			new Thread(() -> {
				nSender.run();
				waiter.countDown();
			}).start();
		}

		try {
			waiter.await();
		} catch (InterruptedException e) {
			log.warning("Failed to wait on sending.");
		}

		assertEquals(amount * 100, entryPoint.getCounter());
	}

	private class Sender implements Runnable {

		private IMonitoringController controller;

		private Sender() {
			final Configuration configuration = ConfigurationFactory.createDefaultConfiguration();
			configuration.setProperty(ConfigurationFactory.METADATA, "false");
			configuration.setProperty(ConfigurationFactory.AUTO_SET_LOGGINGTSTAMP, "true");
			configuration.setProperty(ConfigurationFactory.WRITER_CLASSNAME, SingleSocketTcpWriter.class.getName());
			configuration.setProperty(SingleSocketTcpWriter.CONFIG_FLUSH, "true");
			// configuration.setProperty(WriterController.RECORD_QUEUE_SIZE, "5");
			configuration.setProperty(ConfigurationFactory.TIMER_CLASSNAME, "kieker.monitoring.timer.SystemMilliTimer");
			// configuration.setProperty(AsciiFileWriter.CONFIG_PATH, OUTPATH);
			controller = MonitoringController.createInstance(configuration);
		}

		@Override
		public void run() {
			for (int i = 0; i < 100; i++) {
				ServiceCallRecord rec = new ServiceCallRecord(null, null, null, null, null, null, null, null, null, 0,
						0);
				controller.newMonitoringRecord(rec);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private class ModifiedMonitoringEntryPoint extends MonitoringEntryPoint {

		private int counter = 0;

		protected ModifiedMonitoringEntryPoint() {
			super();
		}

		@Override
		public synchronized void onRecordReceived(IMonitoringRecord record) {
			counter++;
		}

		private int getCounter() {
			return counter;
		}

		private void start() {
			try {
				this.afterPropertiesSet();
			} catch (Exception e) {
				log.warning("Failed to init test.");
			}
		}

	}

}
