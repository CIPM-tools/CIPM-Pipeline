package cipm.consistency.tools.evaluation.scalability.presets;

import java.util.List;

import com.google.common.collect.Lists;

import cipm.consistency.tools.evaluation.scalability.ScalabilityMonitoringDataGeneratorScenario;
import cipm.consistency.tools.evaluation.scalability.generator.AbstractMonitoringDataGenerator;
import cipm.consistency.tools.evaluation.scalability.generator.ServiceCallGenerator;

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
