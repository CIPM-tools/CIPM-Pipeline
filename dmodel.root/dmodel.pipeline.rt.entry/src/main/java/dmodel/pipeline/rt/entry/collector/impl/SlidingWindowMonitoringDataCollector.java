package dmodel.pipeline.rt.entry.collector.impl;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dmodel.pipeline.rt.entry.collector.IMonitoringDataCollector;
import dmodel.pipeline.rt.entry.config.MonitoringDataEntryConfiguration;
import kieker.common.record.IMonitoringRecord;

@Service
public class SlidingWindowMonitoringDataCollector implements IMonitoringDataCollector, InitializingBean {

	@Autowired
	private MonitoringDataEntryConfiguration config;

	private ScheduledExecutorService execService;

	private SortedMap<Long, IMonitoringRecord> recordMap;

	@Override
	public void collect(IMonitoringRecord record) {
		recordMap.put(System.currentTimeMillis(), record);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.recordMap = new TreeMap<>();

		// we need 2 threads at max
		this.execService = Executors.newScheduledThreadPool(2);

		// register trigger
		this.execService.scheduleAtFixedRate(() -> execTrigger(), config.getSlidingWindowTrigger(),
				config.getSlidingWindowTrigger(), TimeUnit.SECONDS);
	}

	private void execTrigger() {
		long currentTime = System.currentTimeMillis();
		// get subset
		List<IMonitoringRecord> collected = this.recordMap
				.subMap(currentTime - config.getSlidingWindowSize() * 1000, currentTime).values().stream()
				.collect(Collectors.toList());
		// TODO pass that to the processing part

		// cut old
		cutRecordMap(currentTime);
	}

	private void cutRecordMap(long currentTime) {
		recordMap.headMap(currentTime - config.getSlidingWindowSize() * 1000).clear();
	}

}
