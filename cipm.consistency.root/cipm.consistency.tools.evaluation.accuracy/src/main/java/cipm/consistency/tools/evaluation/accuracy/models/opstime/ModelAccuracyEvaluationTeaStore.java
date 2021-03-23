package cipm.consistency.tools.evaluation.accuracy.models.opstime;

import java.io.File;
import java.util.List;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.system.System;

import com.google.common.collect.Lists;

import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.pcm.util.PCMUtils;
import cipm.consistency.tools.evaluation.accuracy.models.util.PCMDeploymentComparator;
import cipm.consistency.tools.evaluation.accuracy.models.util.PCMSystemComparator;
import lombok.extern.java.Log;

@Log
public class ModelAccuracyEvaluationTeaStore {

	public static void main(String[] args) {
		PCMUtils.loadPCMModels();

		// we just want to show that the jaccard coeffcient is always one, so one list
		// is enough
		List<Double> jcs = Lists.newArrayList();

		// folders
		File experimentExecutionFolder = new File("test-data/opstime-monitoring-and-models/experiment-executions");
		String modelsPath = "models";
		File experimentScenariosFolder = new File("test-data/opstime-monitoring-and-models/experiment-scenarios");
		String referenceModelsPath = "ref_models";

		// comparators
		PCMSystemComparator comparator = new PCMSystemComparator();
		PCMDeploymentComparator comparator2 = new PCMDeploymentComparator();

		// sync offset
		int syncOffset = 2;
		File[] executionFolders = experimentExecutionFolder.listFiles();
		File[] scenarioFolders = experimentScenariosFolder.listFiles();

		if (executionFolders.length == scenarioFolders.length) {
			for (int i = 0; i < executionFolders.length; i++) {
				File referenceModelContainer = new File(scenarioFolders[i], referenceModelsPath);
				File executionModelContainer = new File(executionFolders[i], modelsPath);
				File[] referenceModelIterations = referenceModelContainer.listFiles();
				for (int k = 0; k < referenceModelIterations.length; k++) {
					File referenceModelSystem = new File(referenceModelIterations[k], "system.system");
					File referenceModelAllocation = new File(referenceModelIterations[k], "allocation.allocation");

					File executionModelSystem = new File(executionModelContainer,
							"system_" + (syncOffset + k) + ".system");
					File exeuctionModelAllocation = new File(executionModelContainer,
							"allocation_" + (syncOffset + k) + ".allocation");

					System actual = ModelUtil.readFromFile(executionModelSystem, System.class);
					System expected = ModelUtil.readFromFile(referenceModelSystem, System.class);

					Allocation actual2 = ModelUtil.readFromFile(exeuctionModelAllocation, Allocation.class);
					Allocation expected2 = ModelUtil.readFromFile(referenceModelAllocation, Allocation.class);

					jcs.add(comparator.compareSystems(actual, expected));
					jcs.add(comparator2.compareAllocations(actual2, expected2));
				}
			}
		}

		// print results
		log.info(jcs.toString());

	}

}
