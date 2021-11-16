package cipm.consistency.tools.evaluation.overhead;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kieker.analysis.plugin.reader.util.IRecordReceivedListener;
import kieker.common.record.IMonitoringRecord;

public class PlainReceiveServer implements IRecordReceivedListener {
	private ExecutorService clientHandlingExecutorService;
	private MultiChannelTcpReader tcpReader;

	public PlainReceiveServer() {
	}

	public void start() {
		clientHandlingExecutorService = Executors.newFixedThreadPool(10); // max 10 concurrent
		tcpReader = new MultiChannelTcpReader(10133, 65535, this, clientHandlingExecutorService);

		System.out.println("Start listener.");
		tcpReader.run();
	}
	
	public void stop() {
		tcpReader.terminate();
	}

	@Override
	public void onRecordReceived(IMonitoringRecord record) {
		// do nothing
	}

}
