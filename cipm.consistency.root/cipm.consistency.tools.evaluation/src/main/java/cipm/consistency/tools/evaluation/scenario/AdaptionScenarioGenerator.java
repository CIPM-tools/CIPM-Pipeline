package cipm.consistency.tools.evaluation.scenario;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.base.shared.pcm.LocalFilesystemPCM;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenario;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioGenerationConfig;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioList;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.MigrationScenario;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.ReplicationScenario;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.SystemChangeScenario;
import cipm.consistency.tools.evaluation.scenario.data.scenarios.UserBehaviorChangeScenario;
import cipm.consistency.tools.evaluation.scenario.data.teastore.LoadProfileType;
import cipm.consistency.tools.evaluation.scenario.data.teastore.MigrationComponentType;
import cipm.consistency.tools.evaluation.scenario.data.teastore.RecommenderType;
import cipm.consistency.tools.evaluation.scenario.data.teastore.ReplicationComponentType;

public class AdaptionScenarioGenerator {
	private static final Random RANDOM = new Random();
	private static final Map<ReplicationComponentType, MigrationComponentType> componentTypesMapping = new HashMap<>() {
		/**
		 * Generated UID.
		 */
		private static final long serialVersionUID = -5858165882043630549L;

		{
			put(ReplicationComponentType.AUTH, MigrationComponentType.AUTH);
			put(ReplicationComponentType.IMAGE, MigrationComponentType.IMAGE);
			put(ReplicationComponentType.PERSISTENCE, MigrationComponentType.PERSISTENCE);
		}
	};

	private Map<MigrationComponentType, Integer> migrationData;
	private RecommenderType currentRecommender;
	private LoadProfileType currentLoadProfile;

	public AdaptionScenarioList generateScenarioList(AdaptionScenarioGenerationConfig config,
			InMemoryPCM initialModel) {
		// clear current data
		migrationData = Maps.newHashMap();
		currentLoadProfile = config.getInitialLoad();
		currentRecommender = config.getInitialRecommender();

		// put initial values into maps
		for (MigrationComponentType mct : MigrationComponentType.values()) {
			migrationData.put(mct, 1);
		}

		// create folder
		File outputFolder = new File(config.getReferenceModelFolder());
		if (!outputFolder.exists() || !outputFolder.isDirectory()) {
			outputFolder.mkdirs();
		}

		AdaptionScenarioList output = new AdaptionScenarioList();

		// initial things
		output.getInitialScenarios().add(new UserBehaviorChangeScenario(config.getInitialLoad()));
		output.getInitialScenarios().add(new SystemChangeScenario(currentRecommender));

		// generate scenarios
		double[] probabilityStack = new double[] { config.getProbMigration(), config.getProbReplication(),
				config.getProbUserChange(), config.getProbSystemChange() };

		InMemoryPCM currentModel = initialModel;
		for (int i = 0; i < config.getScenarioCount(); i++) {
			int selectedScenarioId = selectScenarioTypeFromStack(probabilityStack);
			while (selectedScenarioId == 3 && i >= 15) {
				selectedScenarioId = selectScenarioTypeFromStack(probabilityStack);
			}

			AdaptionScenario scenario = null;
			if (selectedScenarioId == 0) {
				// migration
				scenario = generateMigrationScenario();
			} else if (selectedScenarioId == 1) {
				// replication
				scenario = generateReplicationScenario(config);
			} else if (selectedScenarioId == 3) {
				// recommender change
				scenario = generateRecommenderChangeScenario();
			} else if (selectedScenarioId == 2) {
				// load user change
				scenario = generateLoadChangeScenario();
			}

			if (scenario != null) {
				output.getScenarios().add(scenario);
				currentModel = scenario.generateReferenceModel(currentModel);

				// save the reference model
				File refernceModelOutput = new File(outputFolder, "iteration" + String.valueOf(i));
				refernceModelOutput.mkdirs();

				LocalFilesystemPCM outputPCM = generateReferenceModelPaths(refernceModelOutput);
				currentModel.saveToFilesystem(outputPCM);
			}
		}

		return output;
	}

	private AdaptionScenario generateLoadChangeScenario() {
		return new UserBehaviorChangeScenario(generateNewLoadProfileType(currentLoadProfile));
	}

	private LoadProfileType generateNewLoadProfileType(LoadProfileType currentLoadProfileType) {
		LoadProfileType type = currentLoadProfileType;
		while (type == currentLoadProfileType || type == LoadProfileType.NONE) {
			int index = RANDOM.nextInt(LoadProfileType.values().length);
			type = LoadProfileType.values()[index];
		}
		currentLoadProfile = type;
		return type;
	}

	private AdaptionScenario generateRecommenderChangeScenario() {
		return new SystemChangeScenario(generateNewRecommenderType(currentRecommender));
	}

	private RecommenderType generateNewRecommenderType(RecommenderType currentRecommender) {
		RecommenderType type = currentRecommender;
		while (type == currentRecommender) {
			int index = RANDOM.nextInt(RecommenderType.values().length);
			type = RecommenderType.values()[index];
		}
		currentRecommender = type;
		return type;
	}

	private LocalFilesystemPCM generateReferenceModelPaths(File basePath) {
		LocalFilesystemPCM out = new LocalFilesystemPCM();
		out.setRepositoryFile(new File(basePath, "repository.repository"));
		out.setSystemFile(new File(basePath, "system.system"));
		out.setResourceEnvironmentFile(new File(basePath, "resenv.resourceenvironment"));
		out.setAllocationModelFile(new File(basePath, "allocation.allocation"));
		out.setUsageModelFile(new File(basePath, "usage.usagemodel"));
		return out;
	}

	private ReplicationScenario generateReplicationScenario(AdaptionScenarioGenerationConfig config) {
		ReplicationComponentType selectedType = randomReplicationComponentType();
		int currentInstances = migrationData.get(componentTypesMapping.get(selectedType));
		boolean scaleUp = RANDOM.nextBoolean();
		if (currentInstances == config.getMaxReplications()) {
			scaleUp = false;
		} else if (currentInstances == 1) {
			scaleUp = true;
		}
		int nInstances = scaleUp ? currentInstances + 1 : currentInstances - 1;
		migrationData.put(componentTypesMapping.get(selectedType), nInstances);
		return new ReplicationScenario(selectedType, nInstances);
	}

	private AdaptionScenario generateMigrationScenario() {
		MigrationComponentType selectedType = randomMigrationComponentType();
		int instanceSelection = RANDOM.nextInt(migrationData.get(selectedType));
		return new MigrationScenario(selectedType, instanceSelection);
	}

	private ReplicationComponentType randomReplicationComponentType() {
		int index = RANDOM.nextInt(ReplicationComponentType.values().length);
		return ReplicationComponentType.values()[index];
	}

	private MigrationComponentType randomMigrationComponentType() {
		int index = RANDOM.nextInt(MigrationComponentType.values().length);
		return MigrationComponentType.values()[index];
	}

	private int selectScenarioTypeFromStack(double[] stack) {
		int k = 0;
		double s = 0;
		double r = Math.random();
		for (k = 0; k < stack.length - 1; k++) {
			if (r < s + stack[k]) {
				return k;
			} else {
				s += stack[k];
			}
		}
		return stack.length - 1;
	}

}
