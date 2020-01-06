package dmodel.pipeline.monitoring.controller;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;


public class MonitoringAnalysisData {
    private long serviceCallOverhead = 0;

    private long branchOverhead = 0;

    private long loopOverhead = 0;

    private long internalOverhead = 0;

    public MonitoringAnalysisData() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                File overheadFile = new File("overhead.txt");
                if (!(overheadFile.exists())) {
                    try {
                        overheadFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(overheadFile), StandardCharsets.UTF_8)) {
                    // do stuff
                    writer.append(String.valueOf(serviceCallOverhead));
                    writer.append(System.lineSeparator());
                    writer.append(String.valueOf(branchOverhead));
                    writer.append(System.lineSeparator());
                    writer.append(String.valueOf(loopOverhead));
                    writer.append(System.lineSeparator());
                    writer.append(String.valueOf(internalOverhead));
                    writer.flush();
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public long enterOverhead() {
        return System.nanoTime();
    }

    public synchronized void exitServiceCallOverhead(String serviceId, long start) {
        this.serviceCallOverhead = (System.nanoTime()) - start;
    }

    public synchronized void exitBranchOverhead(String branchId, long start) {
        this.branchOverhead = (System.nanoTime()) - start;
    }

    public synchronized void exitLoopOverhead(String loopId, long start) {
        this.loopOverhead = (System.nanoTime()) - start;
    }

    public synchronized void internalOverhead(String internalActionId, long start) {
        this.internalOverhead = (System.nanoTime()) - start;
    }
}

