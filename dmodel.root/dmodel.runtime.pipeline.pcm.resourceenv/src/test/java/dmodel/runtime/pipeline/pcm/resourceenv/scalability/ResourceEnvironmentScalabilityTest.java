package dmodel.runtime.pipeline.pcm.resourceenv.scalability;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

import dmodel.base.shared.ModelUtil;
import dmodel.base.shared.structure.Tree;
import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.designtime.monitoring.util.MonitoringDataUtil;
import dmodel.runtime.pipeline.AbstractScalabilityTestBase;
import dmodel.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.runtime.pipeline.pcm.resourceenv.AbstractResourceEnvironmentTransformationTestBase;
import dmodel.runtime.pipeline.pcm.resourceenv.ResourceEnvironmentTransformation;
import dmodel.runtime.pipeline.scalability.ScalabilityMonitoringDataGenerator;
import dmodel.runtime.pipeline.scalability.ScalabilityMonitoringDataGeneratorScenario;
import dmodel.runtime.pipeline.scalability.generator.AbstractMonitoringDataGenerator;
import dmodel.runtime.pipeline.scalability.generator.ServiceCallGenerator;
import lombok.extern.java.Log;

@RunWith(SpringRunner.class)
@Log
@Import(AbstractResourceEnvironmentTransformationTestBase.ResourceEnvironmentTransformationTestConfiguration.class)
public class ResourceEnvironmentScalabilityTest extends AbstractScalabilityTestBase {
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
		Map<Integer, Long> combinedMap = Maps.newHashMap();

		for (int i = 4; i <= 40; i += 4) {
			List<String> generatedHostIds = IntStream.range(0, i).mapToObj(t -> UUID.randomUUID().toString())
					.collect(Collectors.toList());
			ScalabilityMonitoringDataGenerator generator = createGeneratorFullyMeshed(generatedHostIds);
			Map<Integer, Long> stats = generateScabailityData(generator, generator.getChilds().size(),
					generator.getChilds().size(), 1);
			combinedMap.put(i, stats.get(generator.getChilds().size()));

			this.resetBefore();
		}

		System.out.println(blackboard.getPcmQuery().getRaw().getResourceEnvironmentModel()
				.getResourceContainer_ResourceEnvironment().size());

		createPlot(combinedMap, "test-results/scalability_resourceenv_fullymeshed.png", "Fully meshed hosts",
				"Amount of hosts", "Execution time in seconds", 1);
		saveRawData(combinedMap, "test-results/scalability_resourceenv_fullymeshed_raw.json");
	}

	@Test
	public void resourceEnvironmentScalabilityTest2() {
		Map<Integer, Long> combinedMap = Maps.newHashMap();
		for (int i = 20; i <= 700; i += 20) {
			loadModelsAndVsum();

			List<String> generatedHostIds = IntStream.range(0, i).mapToObj(t -> UUID.randomUUID().toString())
					.collect(Collectors.toList());
			ScalabilityMonitoringDataGenerator generator = createGeneratorSparse(generatedHostIds);
			Map<Integer, Long> stats = generateScabailityData(generator, generator.getChilds().size(),
					generator.getChilds().size(), 1);
			combinedMap.put(i, stats.get(generator.getChilds().size()));
		}

		createPlot(combinedMap, "test-results/scalability_resourceenv_sparse.png",
				"Sparse meshed hosts (1 connection per host)", "Amount of hosts", "Execution time in seconds", 1);
		saveRawData(combinedMap, "test-results/scalability_resourceenv_sparse_raw.json");
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
