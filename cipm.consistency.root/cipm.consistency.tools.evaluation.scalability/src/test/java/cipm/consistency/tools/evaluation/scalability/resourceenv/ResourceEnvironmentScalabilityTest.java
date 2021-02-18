package cipm.consistency.tools.evaluation.scalability.resourceenv;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.bridge.monitoring.util.MonitoringDataUtil;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.pcm.resourceenv.AbstractResourceEnvironmentTransformationTestBase;
import cipm.consistency.runtime.pipeline.pcm.resourceenv.ResourceEnvironmentTransformation;
import cipm.consistency.tools.evaluation.scalability.AbstractScalabilityTestBase;
import cipm.consistency.tools.evaluation.scalability.ScalabilityMonitoringDataGenerator;
import cipm.consistency.tools.evaluation.scalability.ScalabilityMonitoringDataGeneratorScenario;
import cipm.consistency.tools.evaluation.scalability.generator.AbstractMonitoringDataGenerator;
import cipm.consistency.tools.evaluation.scalability.generator.ServiceCallGenerator;
import lombok.extern.java.Log;

@RunWith(SpringRunner.class)
@Log
@Import(AbstractResourceEnvironmentTransformationTestBase.ResourceEnvironmentTransformationTestConfiguration.class)
public class ResourceEnvironmentScalabilityTest extends AbstractScalabilityTestBase {
	private static final int REPETITIONS = 10;

	@Autowired
	private ResourceEnvironmentTransformation transformation;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	// BEFORE
	@Before
	public void resetBefore() {
		Logger.getRootLogger().setLevel(Level.DEBUG);

		this.loadModelsAndVsum();
		super.reloadVsum();
	}

	public void loadModelsAndVsum() {
		loadModels();
		blackboard.reset();
		transformation.setBlackboard(blackboard);
	}

	protected void loadModels() {
		super.setSpecific(null, null, null);
		super.setPcm(ModelUtil.readFromFile(new File("test-data/scalability/test.repository").getAbsolutePath(),
				Repository.class), null, null, null, null);
	}

	// TESTS
	@Test
	public void resourceEnvironmentScalabilityTest1() throws NoSuchMethodException, SecurityException {
		Map<Integer, Long> outerMap = Maps.newHashMap();

		for (int j = 0; j < REPETITIONS; j++) {
			Map<Integer, Long> combinedMap = Maps.newHashMap();
			for (int i = 4; i <= 20; i += 2) {
				List<String> generatedHostIds = IntStream.range(0, i).mapToObj(t -> UUID.randomUUID().toString())
						.collect(Collectors.toList());
				ScalabilityMonitoringDataGenerator generator = createGeneratorFullyMeshed(generatedHostIds);
				Map<Integer, Long> stats = generateScabailityData(generator, generator.getChilds().size(),
						generator.getChilds().size(), 1);
				combinedMap.put(i, stats.get(generator.getChilds().size()));

				this.resetBefore();
			}

			outerMap = combineMapsMovingAverage(outerMap, combinedMap);
		}

		createPlot(outerMap, "test-results/scalability_resourceenv_fullymeshed.png", "Fully meshed hosts",
				"Amount of hosts", "Execution time in seconds", 1);
		saveRawData(outerMap, "test-results/scalability_resourceenv_fullymeshed_raw.json");
	}

	@Test
	public void resourceEnvironmentScalabilityTest2() {
		Map<Integer, Long> outerMap = Maps.newHashMap();
		for (int j = 0; j < REPETITIONS; j++) {
			Map<Integer, Long> combinedMap = Maps.newHashMap();
			for (int i = 20; i <= 400; i += 20) {
				loadModelsAndVsum();

				List<String> generatedHostIds = IntStream.range(0, i).mapToObj(t -> UUID.randomUUID().toString())
						.collect(Collectors.toList());
				ScalabilityMonitoringDataGenerator generator = createGeneratorSparse(generatedHostIds);
				Map<Integer, Long> stats = generateScabailityData(generator, generator.getChilds().size(),
						generator.getChilds().size(), 1);
				combinedMap.put(i, stats.get(generator.getChilds().size()));
			}

			outerMap = combineMapsMovingAverage(outerMap, combinedMap);
		}

		createPlot(outerMap, "test-results/scalability_resourceenv_sparse.png",
				"Sparse meshed hosts (1 connection per host)", "Amount of hosts", "Execution time in seconds", 1);
		saveRawData(outerMap, "test-results/scalability_resourceenv_sparse_raw.json");
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
	private Map<Integer, Long> generateScabailityData(ScalabilityMonitoringDataGenerator generator, int min, int max,
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

			transformation.deriveResourceEnvironment(tree);

			long after = java.lang.System.nanoTime();

			results.put(i, after - before);
		}

		return results;
	}

	private ScalabilityMonitoringDataGenerator createGeneratorFullyMeshed(List<String> hosts) {
		List<String> idList = getSeffsUnderObservation().stream().map(seff -> seff.getId())
				.collect(Collectors.toList());
		List<ScalabilityMonitoringDataGeneratorScenario> scenarios = Lists.newArrayList();
		for (String hostA : hosts) {
			for (String hostB : hosts) {
				AbstractMonitoringDataGenerator nRoot = ServiceCallGenerator.builder().hostId(hostA).hostName(hostA)
						.serviceIds(idList).min(1).max(1).build();
				AbstractMonitoringDataGenerator nChild = ServiceCallGenerator.builder().hostId(hostB).hostName(hostB)
						.serviceIds(idList).min(1).max(1).build();
				nRoot.addChild(nChild);

				ScalabilityMonitoringDataGeneratorScenario scenario = ScalabilityMonitoringDataGeneratorScenario
						.builder().occurences(1.0f / (float) (hosts.size() * hosts.size()))
						.roots(Lists.newArrayList(nRoot)).build();

				scenarios.add(scenario);
			}
		}

		return ScalabilityMonitoringDataGenerator.builder().childs(scenarios).build();
	}

	private ScalabilityMonitoringDataGenerator createGeneratorSparse(List<String> hosts) {
		Random rand = new Random();
		List<String> idList = getSeffsUnderObservation().stream().map(seff -> seff.getId())
				.collect(Collectors.toList());
		List<ScalabilityMonitoringDataGeneratorScenario> scenarios = Lists.newArrayList();
		for (String hostA : hosts) {
			String hostB = hosts.get(rand.nextInt(hosts.size()));
			while (hostB.equals(hostA)) {
				hostB = hosts.get(rand.nextInt(hosts.size()));
			}

			AbstractMonitoringDataGenerator nRoot = ServiceCallGenerator.builder().hostId(hostA).hostName(hostA)
					.serviceIds(idList).min(1).max(1).build();
			AbstractMonitoringDataGenerator nChild = ServiceCallGenerator.builder().hostId(hostB).hostName(hostB)
					.serviceIds(idList).min(1).max(1).build();
			nRoot.addChild(nChild);

			ScalabilityMonitoringDataGeneratorScenario scenario = ScalabilityMonitoringDataGeneratorScenario.builder()
					.occurences(1.0f / (float) (hosts.size())).roots(Lists.newArrayList(nRoot)).build();

			scenarios.add(scenario);
		}

		return ScalabilityMonitoringDataGenerator.builder().childs(scenarios).build();
	}

	private List<ResourceDemandingSEFF> getSeffsUnderObservation() {
		BasicComponent componentUnderObservation = blackboard.getPcmQuery().getRepository()
				.getElementById("_v1aI8JhLEeqhBu_7urDfig", BasicComponent.class);
		return ModelUtil.getObjects(componentUnderObservation, ResourceDemandingSEFF.class);
	}

}
