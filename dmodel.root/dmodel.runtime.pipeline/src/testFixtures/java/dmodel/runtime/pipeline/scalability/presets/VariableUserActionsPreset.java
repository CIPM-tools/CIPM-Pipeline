package dmodel.runtime.pipeline.scalability.presets;

import java.util.List;

import com.google.common.collect.Lists;

import dmodel.runtime.pipeline.scalability.ScalabilityMonitoringDataGeneratorScenario;
import dmodel.runtime.pipeline.scalability.generator.AbstractMonitoringDataGenerator;
import dmodel.runtime.pipeline.scalability.generator.ServiceCallGenerator;

public class VariableUserActionsPreset extends ScalabilityMonitoringDataGeneratorPreset {
	private List<String> serviceIds;
	private int numberOfActions;
	private boolean random;

	public VariableUserActionsPreset(List<String> serviceIds, int numberOfActions, boolean random) {
		this.serviceIds = serviceIds;
		this.numberOfActions = numberOfActions;
		this.random = random;

		this.build();
	}

	@Override
	protected List<ScalabilityMonitoringDataGeneratorScenario> generateScenarios() {
		List<AbstractMonitoringDataGenerator> scGenerators = Lists.newArrayList();
		for (int i = 0; i < numberOfActions; i++) {
			AbstractMonitoringDataGenerator scGenerator;
			if (random) {
				scGenerator = ServiceCallGenerator.builder().min(1).max(1).serviceIds(serviceIds).build();
			} else {
				scGenerator = ServiceCallGenerator.builder().min(1).max(1)
						.serviceIds(Lists.newArrayList(serviceIds.get(i % serviceIds.size()))).build();
			}
			scGenerators.add(scGenerator);
		}

		ScalabilityMonitoringDataGeneratorScenario scenario = ScalabilityMonitoringDataGeneratorScenario.builder()
				.occurences(1.0f).roots(scGenerators).build();

		return Lists.newArrayList(scenario);
	}

}
