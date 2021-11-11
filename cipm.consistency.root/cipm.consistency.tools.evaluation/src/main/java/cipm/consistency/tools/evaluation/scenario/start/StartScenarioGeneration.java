package cipm.consistency.tools.evaluation.scenario.start;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.base.shared.pcm.LocalFilesystemPCM;
import cipm.consistency.base.shared.pcm.util.PCMUtils;
import cipm.consistency.tools.evaluation.scenario.AdaptionScenarioGenerator;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioGenerationConfig;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioList;

public class StartScenarioGeneration {

	public static void main(String[] args) {
		PCMUtils.loadPCMModels();

		int startCount = 0;
		int scenarioCount = 10;

		for (int i = startCount; i < scenarioCount + startCount; i++) {
			String k = String.valueOf(i + 1);

			AdaptionScenarioGenerationConfig config = AdaptionScenarioGenerationConfig.builder()
					.referenceModelFolder("scenarios/demo_scenario" + k + "/ref_models").build();

			LocalFilesystemPCM teastoreInitialPCM = new LocalFilesystemPCM();
			teastoreInitialPCM.setRepositoryFile(new File("teastore_models/teastore.repository"));
			teastoreInitialPCM.setAllocationModelFile(new File("teastore_models/teastore.allocation"));
			teastoreInitialPCM.setResourceEnvironmentFile(new File("teastore_models/teastore.resourceenvironment"));
			teastoreInitialPCM.setSystemFile(new File("teastore_models/teastore.system"));
			teastoreInitialPCM.setUsageModelFile(new File("teastore_models/teastore.usagemodel"));

			AdaptionScenarioList output = new AdaptionScenarioGenerator().generateScenarioList(config,
					InMemoryPCM.createFromFilesystem(teastoreInitialPCM));

			ObjectMapper mapper = new ObjectMapper();
			try {
				mapper.writeValue(new File("scenarios/demo_scenario" + k + "/scenario.json"), output);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
