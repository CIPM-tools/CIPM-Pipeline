package dmodel.runtime.pipeline.scalability.presets;

import java.util.List;

import com.google.common.collect.Lists;

import dmodel.runtime.pipeline.scalability.ScalabilityMonitoringDataGeneratorScenario;
import dmodel.runtime.pipeline.scalability.generator.AbstractMonitoringDataGenerator;
import dmodel.runtime.pipeline.scalability.generator.ServiceCallGenerator;

public class SingleUserActionPreset extends ScalabilityMonitoringDataGeneratorPreset {
	private String serviceId;

	public SingleUserActionPreset(String serviceId) {
		this.serviceId = serviceId;
		this.build();
	}

	@Override
	protected List<ScalabilityMonitoringDataGeneratorScenario> generateScenarios() {
		AbstractMonitoringDataGenerator scGenerator = ServiceCallGenerator.builder().min(1).max(1)
				.serviceIds(Lists.newArrayList(serviceId)).build();

		ScalabilityMonitoringDataGeneratorScenario scenario = ScalabilityMonitoringDataGeneratorScenario.builder()
				.occurences(1.0f).roots(Lists.newArrayList(scGenerator)).build();

		return Lists.newArrayList(scenario);
	}

}
