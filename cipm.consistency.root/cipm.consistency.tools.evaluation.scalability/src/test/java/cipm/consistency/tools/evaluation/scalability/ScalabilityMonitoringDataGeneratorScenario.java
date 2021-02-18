package cipm.consistency.tools.evaluation.scalability;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.tools.evaluation.scalability.generator.AbstractMonitoringDataGenerator;
import lombok.Data;

@Data
@lombok.Builder
public class ScalabilityMonitoringDataGeneratorScenario {

	private List<AbstractMonitoringDataGenerator> roots;
	private float occurences;

	public List<PCMContextRecord> generateMonitoringData() {
		String generatedSessionID = UUID.randomUUID().toString();
		return roots.stream().map(r -> r.generateData(generatedSessionID)).flatMap(List::stream)
				.collect(Collectors.toList());
	}

}
