package cipm.consistency.tools.evaluation.accuracy.models.devtime;

import java.io.File;

import org.palladiosimulator.pcm.system.System;

import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.pcm.util.PCMUtils;
import cipm.consistency.tools.evaluation.accuracy.models.util.PCMSystemComparator;
import lombok.extern.java.Log;

@Log
public class SystemExtractionEvaluationTeaStore {

	public static void main(String[] args) {
		PCMUtils.loadPCMModels();

		// define paths
		String referenceModelPath = "test-data/devtime-system-extraction/teastore-reference.system";
		String extractedModelPath = "test-data/devtime-system-extraction/teastore-extracted.system";

		// load models
		System referenceSystemModel = ModelUtil.readFromFile(new File(referenceModelPath), System.class);
		System extractedSystemModel = ModelUtil.readFromFile(new File(extractedModelPath), System.class);

		// calculate similarity (jaccard coefficient)
		PCMSystemComparator comparator = new PCMSystemComparator();
		double jaccardResult = comparator.compareSystems(extractedSystemModel, referenceSystemModel);

		// print results
		log.info("The comparison of the extracted model to the reference model of TeaStore is finished.");
		log.info("The result is a Jaccard coefficient (JC) of " + String.valueOf(jaccardResult) + "!");

	}

}
