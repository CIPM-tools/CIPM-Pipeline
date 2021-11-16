package cipm.consistency.runtime.pipeline.entry.receiver;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cipm.consistency.runtime.pipeline.entry.monitoring.MultiChannelTcpReader;
import kieker.analysis.plugin.reader.util.IRecordReceivedListener;
import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.writer.filesystem.AsciiFileWriter;

public class PlainReceiveAndStoreServer implements IRecordReceivedListener {
	private ExecutorService clientHandlingExecutorService;
	private MultiChannelTcpReader tcpReader;

	private IMonitoringController controller;

	public static void main(String[] args) {
		new PlainReceiveAndStoreServer().start();
	}
	
	public PlainReceiveAndStoreServer(File file) {
		this.controller = getMonitoringController(file);
	}

	public PlainReceiveAndStoreServer() {
		this.controller = getMonitoringController(new File("received"));
	}

	private void start() {
		clientHandlingExecutorService = Executors.newFixedThreadPool(10); // max 10 concurrent
		tcpReader = new MultiChannelTcpReader(10133, 65535, this, clientHandlingExecutorService);

		System.out.println("Start listener.");
		tcpReader.run();
	}

	@Override
	public void onRecordReceived(IMonitoringRecord record) {
		this.controller.newMonitoringRecord(record);
	}

	private MonitoringController getMonitoringController(File file) {
		if (!file.exists()) {
			file.mkdirs();
		}

		final Configuration configuration = ConfigurationFactory.createDefaultConfiguration();
		configuration.setProperty(ConfigurationFactory.METADATA, "false");
		configuration.setProperty(ConfigurationFactory.AUTO_SET_LOGGINGTSTAMP, "true");
		configuration.setProperty(ConfigurationFactory.WRITER_CLASSNAME, AsciiFileWriter.class.getName());
		// configuration.setProperty(WriterController.RECORD_QUEUE_SIZE, "5");
		configuration.setProperty(AsciiFileWriter.CONFIG_FLUSH, "true");
		configuration.setProperty(ConfigurationFactory.TIMER_CLASSNAME, "kieker.monitoring.timer.SystemMilliTimer");
		configuration.setProperty(AsciiFileWriter.CONFIG_PATH, file.getAbsolutePath());
		configuration.setProperty(AsciiFileWriter.CONFIG_MAXENTRIESINFILE, 2000 * 1000 * 100);

		return MonitoringController.createInstance(configuration);
	}

}
