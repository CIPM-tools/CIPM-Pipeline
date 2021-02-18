package cipm.consistency.runtime.pipeline.entry.collector.impl;

import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import cipm.consistency.base.core.config.ConfigurationContainer;
import cipm.consistency.base.core.config.MonitoringDataEntryConfiguration;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.data.SessionPartionedMonitoringData;
import cipm.consistency.runtime.pipeline.entry.collector.IMonitoringDataCollector;
import cipm.consistency.runtime.pipeline.entry.core.IterativeRuntimePipeline;
import cipm.consistency.runtime.pipeline.entry.core.IterativeRuntimePipelineListener;
import kieker.common.record.IMonitoringRecord;
import lombok.extern.java.Log;

@Component
@Log
public class LogarithmicSlidingWindowDataCollector
		implements IMonitoringDataCollector, InitializingBean, IterativeRuntimePipelineListener {
	private MonitoringDataEntryConfiguration config;

	@Autowired
	private IterativeRuntimePipeline pipeline;

	private ScheduledExecutorService execService;

	private SortedMap<Long, List<IMonitoringRecord>> recordMap;

	private List<Pair<IMonitoringRecord, Long>> buffer = Lists.newArrayList();
	private volatile boolean processing = false;

	@Autowired
	private ConfigurationContainer parentConfig;

	@Override
	public void iterationFinished() {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void collect(IMonitoringRecord record) {
		// TODO Auto-generated method stub

	}

	private void execTrigger() {
		processing = true;
		long currentTime = System.currentTimeMillis();
		// get subset
		try {
			// TODO
			List<IMonitoringRecord> collected = this.recordMap
					.subMap(currentTime - config.getSlidingWindowSize() * 1000, currentTime).entrySet().stream()
					.map(e -> e.getValue()).flatMap(List::stream).collect(Collectors.toList());

			// log
			log.info("Triggering the runtime pipeline.");

			// filter it
			List<PCMContextRecord> pcmContextRecords = collected.stream().filter(r -> r instanceof PCMContextRecord)
					.map(PCMContextRecord.class::cast).collect(Collectors.toList());

			// pass it to the processing part
			pipeline.triggerPipeline(
					new SessionPartionedMonitoringData(pcmContextRecords, parentConfig.getVfl().getValidationShare()));

			// cut old
			// TODO

			// open up
			processing = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
