package cipm.consistency.tools.evaluation.scalability.usagemodel;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.system.System;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.bridge.monitoring.util.MonitoringDataUtil;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.AbstractBaseUsageModelDerivationTest;
import cipm.consistency.runtime.pipeline.pcm.usagemodel.transformation.UsageDataDerivation;
import cipm.consistency.tools.evaluation.scalability.AbstractScalabilityTestBase;
import cipm.consistency.tools.evaluation.scalability.ScalabilityMonitoringDataGenerator;
import cipm.consistency.tools.evaluation.scalability.presets.SingleUserActionPreset;
import cipm.consistency.tools.evaluation.scalability.presets.VariableUserActionsPreset;
import lombok.extern.java.Log;

@RunWith(SpringRunner.class)
@Log
@Import(AbstractBaseUsageModelDerivationTest.UsageTransformationTestConfiguration.class)
public class UsageModelTransformationScalabilityTest extends AbstractScalabilityTestBase {
	private static final int REPETITIONS = 15;

	@Autowired
	protected UsageDataDerivation derivation;

	@Autowired
	protected IPcmModelProvider pcm;

	// BEFORE
	@Before
	public void loadModelsAndVsum() {
		loadModels();
		super.reloadVsum();
	}

	protected void loadModels() {
		super.setSpecific(null, null, null);
		super.setPcm(
				ModelUtil.readFromFile(new File("test-data/scalability/test.repository").getAbsolutePath(),
						Repository.class),
				ModelUtil.readFromFile(new File("test-data/scalability/test.system").getAbsolutePath(), System.class),
				ModelUtil.readFromFile(new File("test-data/scalability/test.resourceenvironment").getAbsolutePath(),
						ResourceEnvironment.class),
				ModelUtil.readFromFile(new File("test-data/scalability/test.allocation").getAbsolutePath(),
						Allocation.class),
				null);
	}

	// TESTS
	@Test
	public void singleActionPerUserTest() {
		Map<Integer, Long> flattedResults = Maps.newHashMap();
		for (int i = 0; i < REPETITIONS; i++) {
			Map<Integer, Long> stats = this.generateScalabilityData(
					new SingleUserActionPreset(getSeffsUnderObservation().get(0).getId()), 0, 1500000, 100000);
			flattedResults = combineMapsMovingAverage(flattedResults, stats);
		}

		createPlot(flattedResults, "test-results/scalability/scalability_1user1action.png", "One user action per session",
				"User count (10^3)", "Execution time in seconds", 1000);
		saveRawData(flattedResults, "test-results/scalability/scalability_1user1action_raw.json");
	}

	@Test
	public void variableActionsPerUserTest() {
		List<String> serviceIds = getSeffsUnderObservation().stream().map(seff -> seff.getId())
				.collect(Collectors.toList());

		Map<Integer, Long> flattedResults = Maps.newHashMap();
		for (int j = 0; j < REPETITIONS; j++) {
			Map<Integer, Long> combinedMap = Maps.newHashMap();
			int selectedUserSessions = 2;
			for (int i = 1; i <= Math.pow(10, 5) + 1; i *= 10) {
				java.lang.System.out.println("Current: " + i);
				ScalabilityMonitoringDataGenerator generator = new VariableUserActionsPreset(serviceIds, i, true);
				combinedMap.put(i, generateScalabilityData(generator, selectedUserSessions, selectedUserSessions, 1)
						.get(selectedUserSessions));
			}
			flattedResults = combineMapsMovingAverage(flattedResults, combinedMap);
		}

		createPlot(flattedResults, "test-results/scalability/scalability_1userNactions.png",
				"N user actions per session (1 user)", "Amount of actions per session",
				"Execution time in seconds", 1, true, true);
		saveRawData(flattedResults, "test-results/scalability/scalability_1userNactions_raw.json");
	}

	private Map<Integer, Long> combineMapsMovingAverage(Map<Integer, Long> map1, Map<Integer, Long> map2) {
		Map<Integer, Long> finalMap = Maps.newHashMap();
		Set<Integer> allKeys = Sets.newHashSet();
		allKeys.addAll(map1.keySet());
		allKeys.addAll(map2.keySet());

		for (int key : allKeys) {
			long valueA = map1.containsKey(key) ? map1.get(key) : 0L;
			long valueB = map2.containsKey(key) ? map2.get(key) : 0L;

			finalMap.put(key, Math.round((valueA + valueB) / 2d));
		}

		return finalMap;
	}

	// SCALABILITY RUNNER
	private Map<Integer, Long> generateScalabilityData(ScalabilityMonitoringDataGenerator generator, int min, int max,
			int step) {
		Map<Integer, Long> results = Maps.newHashMap();
		for (int i = min; i <= max; i += step) {
			List<PCMContextRecord> monitoringData = generateMonitoringData(generator, i);
			List<ServiceCallRecord> monitoringDataFiltered = monitoringData.stream()
					.filter(r -> r instanceof ServiceCallRecord).map(ServiceCallRecord.class::cast)
					.collect(Collectors.toList());

			log.info("Number of input records: " + monitoringDataFiltered.size());
			List<Tree<ServiceCallRecord>> tree = MonitoringDataUtil.buildServiceCallTree(monitoringDataFiltered);
			log.info("Number of input trees: " + tree.size());

			long before = java.lang.System.nanoTime();

			derivation.deriveUsageData(tree, blackboard.getPcmQuery(), null);

			long after = java.lang.System.nanoTime();

			results.put(i, after - before);
		}
		return results;
	}

	private List<ResourceDemandingSEFF> getSeffsUnderObservation() {
		BasicComponent componentUnderObservation = blackboard.getPcmQuery().getRepository()
				.getElementById("_v1aI8JhLEeqhBu_7urDfig", BasicComponent.class);
		return ModelUtil.getObjects(componentUnderObservation, ResourceDemandingSEFF.class);
	}

}
