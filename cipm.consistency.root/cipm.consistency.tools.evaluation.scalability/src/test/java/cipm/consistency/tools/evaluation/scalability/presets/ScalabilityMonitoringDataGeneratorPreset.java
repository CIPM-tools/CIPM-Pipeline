package cipm.consistency.tools.evaluation.scalability.presets;

import java.util.List;

import cipm.consistency.tools.evaluation.scalability.ScalabilityMonitoringDataGenerator;
import cipm.consistency.tools.evaluation.scalability.ScalabilityMonitoringDataGeneratorScenario;

public abstract class ScalabilityMonitoringDataGeneratorPreset extends ScalabilityMonitoringDataGenerator {

	protected void build() {
		this.setChilds(generateScenarios());
	}

	protected abstract List<ScalabilityMonitoringDataGeneratorScenario> generateScenarios();

}
