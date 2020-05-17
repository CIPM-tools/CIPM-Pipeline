package dmodel.runtime.pipeline.pcm.usagemodel.scalability;

import java.io.File;
import java.util.List;
import java.util.Map;
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

import dmodel.base.core.IPcmModelProvider;
import dmodel.base.shared.ModelUtil;
import dmodel.base.shared.structure.Tree;
import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.designtime.monitoring.util.MonitoringDataUtil;
import dmodel.runtime.pipeline.AbstractScalabilityTestBase;
import dmodel.runtime.pipeline.pcm.usagemodel.AbstractBaseUsageModelDerivationTest;
import dmodel.runtime.pipeline.scalability.ScalabilityMonitoringDataGenerator;
import dmodel.runtime.pipeline.scalability.presets.SingleUserActionPreset;
import dmodel.runtime.pipeline.scalability.presets.VariableUserActionsPreset;
import dmodel.runtime.pipelinepcm.usagemodel.transformation.UsageDataDerivation;
import lombok.extern.java.Log;

@RunWith(SpringRunner.class)
@Log
@Import(AbstractBaseUsageModelDerivationTest.UsageTransformationTestConfiguration.class)
public class UsageModelTransformationScalabilityTest extends AbstractScalabilityTestBase {
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
		Map<Integer, Long> stats = this.generateScalabilityData(
				new SingleUserActionPreset(getSeffsUnderObservation().get(0).getId()), 0, 2500000, 100000);

		createPlot(stats, "test-results/scalability_1user1action.png", "One user action per session",
				"User count (10^3)", "Execution time in seconds", 1000);
		saveRawData(stats, "test/results/scalability_1user1action_raw.json");
	}

	@Test
	public void variableActionsPerUserTest() {
		List<String> serviceIds = getSeffsUnderObservation().stream().map(seff -> seff.getId())
				.collect(Collectors.toList());

		Map<Integer, Long> combinedMap = Maps.newHashMap();
		int selectedUserSessions = 25000;
		for (int i = 0; i <= 100; i += 5) {
			ScalabilityMonitoringDataGenerator generator = new VariableUserActionsPreset(serviceIds, i, true);
			combinedMap.put(i, generateScalabilityData(generator, selectedUserSessions, selectedUserSessions, 1)
					.get(selectedUserSessions));
		}

		createPlot(combinedMap, "test-results/scalability_1userNactions.png", "N user actions per session",
				"Amount of actions per session", "Execution time in seconds", 1);
		saveRawData(combinedMap, "test-results/scalability_1userNactions_raw.json");
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
