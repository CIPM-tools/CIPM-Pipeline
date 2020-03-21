package dmodel.pipeline.rt.entry.collector.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import dmodel.pipeline.rt.entry.collector.IMonitoringDataCollector;
import dmodel.pipeline.rt.entry.core.IterativeRuntimePipeline;
import dmodel.pipeline.rt.entry.core.IterativeRuntimePipelineListener;
import dmodel.pipeline.rt.pipeline.data.PartitionedMonitoringData;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import dmodel.pipeline.shared.config.MonitoringDataEntryConfiguration;
import kieker.common.record.IMonitoringRecord;
import lombok.extern.java.Log;

@Component
@Log
public class SlidingWindowMonitoringDataCollector
		implements IMonitoringDataCollector, InitializingBean, IterativeRuntimePipelineListener {

	private MonitoringDataEntryConfiguration config;

	@Autowired
	private DModelConfigurationContainer parentConfig;

	@Autowired
	private IterativeRuntimePipeline pipeline;

	private ScheduledExecutorService execService;

	private SortedMap<Long, List<IMonitoringRecord>> recordMap;

	private List<Pair<IMonitoringRecord, Long>> buffer = Lists.newArrayList();
	private volatile boolean processing = false;

	@Override
	public void collect(IMonitoringRecord record) {
		if (processing) {
			buffer.add(Pair.of(record, System.currentTimeMillis()));
			return;
		}

		synchronized (recordMap) {
			long time = System.currentTimeMillis();
			pushRecord(record, time);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// extract config
		this.config = this.parentConfig.getEntry();

		// record map
		this.recordMap = new TreeMap<>();

		// we need 2 threads at max
		this.execService = Executors.newScheduledThreadPool(2);

		// trigger time delayed
		this.execService.schedule(() -> execTrigger(), config.getSlidingWindowTrigger(), TimeUnit.SECONDS);

		// add listener
		this.pipeline.addPipelineListener(this);
	}

	@Override
	public void iterationFinished() {
		// trigger time delayed
		this.execService.schedule(() -> execTrigger(), config.getSlidingWindowTrigger(), TimeUnit.SECONDS);
	}

	private void execTrigger() {
		processing = true;
		long currentTime = System.currentTimeMillis();
		// get subset
		try {
			List<IMonitoringRecord> collected = this.recordMap
					.subMap(currentTime - config.getSlidingWindowSize() * 1000, currentTime).entrySet().stream()
					.map(e -> e.getValue()).flatMap(List::stream).collect(Collectors.toList());

			// log
			log.info("Triggering the runtime pipeline.");

			// pass it to the processing part
			pipeline.triggerPipeline(new PartitionedMonitoringData<IMonitoringRecord>(collected,
					parentConfig.getVfl().getValidationShare()));

			// cut old
			cutRecordMap(currentTime);

			// open up
			processing = false;
			flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void pushRecord(IMonitoringRecord record, long time) {
		if (recordMap.containsKey(time)) {
			recordMap.get(time).add(record);
		} else {
			List<IMonitoringRecord> nList = new LinkedList<>();
			nList.add(record);
			recordMap.put(time, nList);
		}
	}

	private void flushBuffer() {
		synchronized (recordMap) {
			for (Pair<IMonitoringRecord, Long> rec : buffer) {
				pushRecord(rec.getLeft(), rec.getRight());
			}
			buffer.clear();
		}
	}

	private void cutRecordMap(long currentTime) {
		recordMap.headMap(currentTime - config.getSlidingWindowSize() * 1000).clear();
	}

}
