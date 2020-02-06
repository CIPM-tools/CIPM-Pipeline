package dmodel.pipeline.monitoring.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class MonitoringAnalysisData {
	private static final String OVERHEAD_FILE = "overhead.txt";

	private long serviceCallOverhead = 0;

	private long branchOverhead = 0;

	private long loopOverhead = 0;

	private long internalOverhead = 0;

	private long serviceCalls = 0;
	private long branches = 0;
	private long loops = 0;
	private long internals = 0;

	private float avgServiceCall = 0;
	private float avgBranches = 0;
	private float avgLoops = 0;
	private float avgInternals = 0;

	public MonitoringAnalysisData() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				writeOverhead();
			}
		});
	}

	public long enterOverhead() {
		return System.nanoTime();
	}

	public synchronized void exitServiceCallOverhead(String serviceId, long start) {
		long duration = (System.nanoTime()) - start;
		this.serviceCallOverhead += duration;
		this.serviceCalls++;

		this.avgServiceCall += duration;
		if (this.avgServiceCall > 0) {
			this.avgServiceCall /= 2;
		}
	}

	public synchronized void exitBranchOverhead(String branchId, long start) {
		long duration = (System.nanoTime()) - start;
		this.branchOverhead += duration;
		this.branches++;

		this.avgBranches += duration;
		if (this.avgBranches > 0) {
			this.avgBranches /= 2;
		}
	}

	public synchronized void exitLoopOverhead(String loopId, long start) {
		long duration = (System.nanoTime()) - start;
		this.loopOverhead += duration;
		this.loops++;

		this.avgLoops += duration;
		if (this.avgLoops > 0) {
			this.avgLoops /= 2;
		}
	}

	public synchronized void internalOverhead(String internalActionId, long start) {
		long duration = (System.nanoTime()) - start;
		this.internalOverhead += duration;
		this.internals++;

		this.avgInternals += duration;
		if (this.avgInternals > 0) {
			this.avgInternals /= 2;
		}
	}

	public void writeOverhead() {
		File overheadFile = new File(OVERHEAD_FILE);
		if (!(overheadFile.exists())) {
			try {
				overheadFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(overheadFile, true),
				StandardCharsets.UTF_8)) {
			// do stuff
			writer.append(String.valueOf(System.currentTimeMillis()));
			writer.append(";");
			writer.append(String.valueOf(serviceCallOverhead));
			writer.append(";");
			writer.append(String.valueOf(branchOverhead));
			writer.append(";");
			writer.append(String.valueOf(loopOverhead));
			writer.append(";");
			writer.append(String.valueOf(internalOverhead));
			writer.append(";");
			writer.append(String.valueOf(serviceCalls));
			writer.append(";");
			writer.append(String.valueOf(branches));
			writer.append(";");
			writer.append(String.valueOf(loops));
			writer.append(";");
			writer.append(String.valueOf(internals));
			writer.append(";");
			writer.append(String.valueOf(avgServiceCall));
			writer.append(";");
			writer.append(String.valueOf(avgBranches));
			writer.append(";");
			writer.append(String.valueOf(avgLoops));
			writer.append(";");
			writer.append(String.valueOf(avgServiceCall));
			writer.append(System.lineSeparator());
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
