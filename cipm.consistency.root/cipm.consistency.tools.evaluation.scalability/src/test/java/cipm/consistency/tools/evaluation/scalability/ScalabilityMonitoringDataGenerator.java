package cipm.consistency.tools.evaluation.scalability;

import java.util.List;

import com.google.common.collect.Lists;

import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScalabilityMonitoringDataGenerator {

	private List<ScalabilityMonitoringDataGeneratorScenario> childs;

	public List<PCMContextRecord> generateMonitoringData(int amount) {
		List<PCMContextRecord> result = Lists.newArrayList();

		childs.stream().forEach(child -> {
			int toGenerate = (int) Math.round(amount * child.getOccurences());
			for (int i = 0; i < toGenerate; i++) {
				result.addAll(child.generateMonitoringData());
			}
		});

		return result;
	}

}
