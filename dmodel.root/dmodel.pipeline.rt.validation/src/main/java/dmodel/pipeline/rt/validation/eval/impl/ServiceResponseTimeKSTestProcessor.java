package dmodel.pipeline.rt.validation.eval.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pcm.headless.shared.data.results.InMemoryResultRepository;
import org.pcm.headless.shared.data.results.PlainMetricMeasuringPointBundle;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.records.ServiceContextRecord;
import dmodel.pipeline.rt.validation.data.ValidationMetric;
import dmodel.pipeline.rt.validation.data.impl.KSTestMetric;
import dmodel.pipeline.rt.validation.eval.IAnalysisDataProcessor;
import dmodel.pipeline.rt.validation.eval.IMonitoringDataProcessor;

@Component
public class ServiceResponseTimeKSTestProcessor implements IAnalysisDataProcessor, IMonitoringDataProcessor, InitializingBean {
	private Map<String, KSTestMetric> metricMapping;

	@Override
	public List<ValidationMetric> getValidationMetrics() {
		return metricMapping.entrySet().stream().map(e -> new ValidationMetric(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}

	@Override
	public void clearValidationMetrics() {
		metricMapping.clear();
	}

	@Override
	public void processMonitoringRecord(ServiceContextRecord record) {
		if (record instanceof ServiceCallRecord) {
			ServiceCallRecord scr = (ServiceCallRecord) record;
			if (!metricMapping.containsKey(scr.getServiceId())) {
				metricMapping.put(scr.getServiceId(), new KSTestMetric());
			}
			metricMapping.get(scr.getServiceId()).addValueB(scr.getExitTime() - scr.getEntryTime());
		}
	}

	@Override
	public void processAnalysisData(InMemoryResultRepository results) {
		results.getValues().forEach(measuringPoint -> {
			PlainMetricMeasuringPointBundle point = measuringPoint.getKey();
			// TODO
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		metricMapping = Maps.newHashMap();
	}

}
