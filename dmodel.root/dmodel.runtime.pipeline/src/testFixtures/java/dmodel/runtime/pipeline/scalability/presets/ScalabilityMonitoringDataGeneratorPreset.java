package dmodel.runtime.pipeline.scalability.presets;

import java.util.List;

import dmodel.runtime.pipeline.scalability.ScalabilityMonitoringDataGenerator;
import dmodel.runtime.pipeline.scalability.ScalabilityMonitoringDataGeneratorScenario;

public abstract class ScalabilityMonitoringDataGeneratorPreset extends ScalabilityMonitoringDataGenerator {

	protected void build() {
		this.setChilds(generateScenarios());
	}

	protected abstract List<ScalabilityMonitoringDataGeneratorScenario> generateScenarios();

}
