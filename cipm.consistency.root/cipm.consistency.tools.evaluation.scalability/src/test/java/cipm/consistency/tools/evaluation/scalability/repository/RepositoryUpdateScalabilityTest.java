package cipm.consistency.tools.evaluation.scalability.repository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.resourcetype.ResourceRepository;
import org.palladiosimulator.pcm.resourcetype.ResourcetypeFactory;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;
import org.palladiosimulator.pcm.seff.seff_performance.SeffPerformanceFactory;
import org.pcm.headless.api.util.ModelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.core.facade.IPCMQueryFacade;
import cipm.consistency.base.core.facade.IRuntimeEnvironmentQueryFacade;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import cipm.consistency.base.vsum.facade.ISpecificVsumFacade;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.data.PCMPartionedMonitoringData;
import cipm.consistency.runtime.pipeline.pcm.repository.AbstractRepositoryTransformationTestBase;
import cipm.consistency.runtime.pipeline.pcm.repository.IRepositoryCalibration;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;
import cipm.consistency.tools.evaluation.scalability.AbstractScalabilityTestBase;
import cipm.consistency.tools.evaluation.scalability.ScalabilityMonitoringDataGenerator;
import cipm.consistency.tools.evaluation.scalability.ScalabilityMonitoringDataGeneratorScenario;
import cipm.consistency.tools.evaluation.scalability.generator.AbstractMonitoringDataGenerator;
import cipm.consistency.tools.evaluation.scalability.generator.InternalActionGenerator;
import cipm.consistency.tools.evaluation.scalability.generator.ResourceUsageGenerator;
import cipm.consistency.tools.evaluation.scalability.generator.ServiceCallGenerator;
import cipm.consistency.tools.evaluation.scalability.generator.StartStopTimeGenerator;

@RunWith(SpringRunner.class)
@Import(AbstractRepositoryTransformationTestBase.RepositoryTransformationTestConfiguration.class)
public class RepositoryUpdateScalabilityTest extends AbstractScalabilityTestBase {
	private static final int MAX_INTERNAL_ACTIONS = 1000;
	private static final int REPETITIONS = 5;
	private static final String RESOURCE_ID_CPU = "_oro4gG3fEdy4YaaT-RYrLQ";

	@Autowired
	private IRepositoryCalibration transformation;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private IPcmModelProvider pcm;

	@Autowired
	private IPCMQueryFacade pcmFacade;

	@Autowired
	private IRuntimeEnvironmentQueryFacade remQuery;

	@Autowired
	private ISpecificVsumFacade vsumFacade;

	@Before
	public void resetBefore() {
		blackboard.reset(true);
	}

	@AfterClass
	public static void clearAfter() {
		new File("temp.resourcerepository").delete();
	}

	@Test
	public void internalScalabilityTest() {
		List<InternalAction> ias = buildTestPCM(MAX_INTERNAL_ACTIONS);

		Map<Integer, Long> scalabilityData = Maps.newHashMap();
		for (int i = 0; i < REPETITIONS; i++) {
			// create generator
			ScalabilityMonitoringDataGenerator generator = createGenerator(ias, 1);

			// simulate
			Map<Integer, Long> inner = this.generateScabailityData(generator, 25000, 1000000, 25000);
			scalabilityData = combineMapsMovingAverage(scalabilityData, inner);
		}

		this.createPlot(scalabilityData, "test-results/scalability/scalability_repository_internal.png",
				"One internal action, n times", "Number of executions of the internal action (^3)",
				"Execution time (s)", 1000);
		this.saveRawData(scalabilityData, "test-results/scalability/scalability_repository_internal_raw.json");
	}

	@Test
	public void overallScalabilityTest() {
		List<InternalAction> ias = buildTestPCM(MAX_INTERNAL_ACTIONS);

		// create generator
		Map<Integer, Long> outerMap = Maps.newHashMap();
		for (int j = 0; j < REPETITIONS; j++) {
			Map<Integer, Long> combinedMap = Maps.newHashMap();
			for (int i = MAX_INTERNAL_ACTIONS / 20; i <= MAX_INTERNAL_ACTIONS; i += MAX_INTERNAL_ACTIONS / 20) {
				ScalabilityMonitoringDataGenerator generator = createGeneratorDifferentActions(ias, i);
				Map<Integer, Long> stats = generateScabailityData(generator, generator.getChilds().size() * 1000,
						generator.getChilds().size() * 1000, 1);
				combinedMap.put(i, stats.get(generator.getChilds().size() * 1000));
			}
			outerMap = combineMapsMovingAverage(outerMap, combinedMap);
		}

		// plot
		this.createPlot(outerMap, "test-results/scalability/scalability_repository_overall.png",
				"n internal action, 1000 times", "Number of internal actions", "Execution time (s)", 1);
		this.saveRawData(outerMap, "test-results/scalability/scalability_repository_overall_raw.json");
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

	private List<InternalAction> buildTestPCM(int size) {
		setSpecific(null, null, null);
		setPcm(null, null, null, null, null);

		// processing resource type
		ResourceRepository repo = ResourcetypeFactory.eINSTANCE.createResourceRepository();
		ProcessingResourceType cpu = ResourcetypeFactory.eINSTANCE.createProcessingResourceType();
		cpu.setId(RESOURCE_ID_CPU);
		repo.getAvailableResourceTypes_ResourceRepository().add(cpu);
		ModelUtil.saveToFile(repo, "temp.resourcerepository");

		// create a service with n internal actions
		OperationInterface iface = RepositoryFactory.eINSTANCE.createOperationInterface();
		OperationSignature sig = RepositoryFactory.eINSTANCE.createOperationSignature();
		iface.getSignatures__OperationInterface().add(sig);

		BasicComponent comp = RepositoryFactory.eINSTANCE.createBasicComponent();

		OperationProvidedRole role = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		role.setProvidedInterface__OperationProvidedRole(iface);

		comp.getProvidedRoles_InterfaceProvidingEntity().add(role);
		pcm.getRepository().getInterfaces__Repository().add(iface);
		pcm.getRepository().getComponents__Repository().add(comp);

		ResourceDemandingSEFF seff = SeffFactory.eINSTANCE.createResourceDemandingSEFF();
		seff.setDescribedService__SEFF(sig);
		comp.getServiceEffectSpecifications__BasicComponent().add(seff);
		List<InternalAction> output = Lists.newArrayList();
		for (int i = 0; i < size; i++) {
			InternalAction action = SeffFactory.eINSTANCE.createInternalAction();
			ParametricResourceDemand demand = SeffPerformanceFactory.eINSTANCE.createParametricResourceDemand();
			demand.setRequiredResource_ParametricResourceDemand(cpu);
			demand.setSpecification_ParametericResourceDemand(CoreFactory.eINSTANCE.createPCMRandomVariable());
			action.getResourceDemand_Action().add(demand);

			seff.getSteps_Behaviour().add(action);
			output.add(action);
		}
		super.reloadVsum();

		// create a single resource container
		RuntimeResourceContainer rc = remQuery.createResourceContainer("host", "host");

		// deploy it
		AssemblyContext actx = pcmFacade.getSystem().createAssemblyContext(comp);
		pcmFacade.getAllocation().deployAssembly(actx, vsumFacade.getCorrespondingResourceContainer(rc).get());

		return output;
	}

	// SCALABILITY RUNNER
	private Map<Integer, Long> generateScabailityData(ScalabilityMonitoringDataGenerator generator, int min, int max,
			int step) {
		Map<Integer, Long> results = Maps.newHashMap();

		for (int i = min; i <= max; i += step) {
			List<PCMContextRecord> monitoringData = generateMonitoringData(generator, i);

			transformation.reset();
			long before = java.lang.System.nanoTime();
			transformation.calibrateRepository(new PCMPartionedMonitoringData(monitoringData, 0.0f).getAllData(),
					pcmFacade, new ValidationData(), Sets.newHashSet());
			long after = java.lang.System.nanoTime();

			results.put(i, after - before);
		}

		return results;
	}

	private ScalabilityMonitoringDataGenerator createGeneratorDifferentActions(List<InternalAction> actions,
			int differentActions) {
		ResourceDemandingSEFF seff = (ResourceDemandingSEFF) actions.get(0).eContainer();
		List<ScalabilityMonitoringDataGeneratorScenario> scenarios = Lists.newArrayList();

		StartStopTimeGenerator generator1 = new EasyDistinctIntervalCreator(50000000);
		StartStopTimeGenerator generator2 = new EasyDistinctIntervalCreator(50000000);
		StartStopTimeGenerator generator3 = new EasyDistinctIntervalCreator(50000000);

		for (int i = 0; i < differentActions; i++) {
			AbstractMonitoringDataGenerator nRoot = ServiceCallGenerator.builder().hostId("host").hostName("host")
					.serviceIds(Lists.newArrayList(seff.getId())).intervalGenerator(Optional.of(generator1)).min(1)
					.max(1).build();
			AbstractMonitoringDataGenerator nChild = InternalActionGenerator.builder()
					.intervalGenerator(Optional.of(generator2)).min(1).max(1).internalActionId(actions.get(i).getId())
					.resourceId(RESOURCE_ID_CPU).build();

			AbstractMonitoringDataGenerator nRoot2 = ResourceUsageGenerator.builder()
					.intervalGenerator(Optional.of(generator3)).hostId("host").hostName("host").min(1).max(1)
					.resourceId(RESOURCE_ID_CPU).usageMin(1).usageMax(1).build();

			nRoot.addChild(nChild);

			ScalabilityMonitoringDataGeneratorScenario scenario = ScalabilityMonitoringDataGeneratorScenario.builder()
					.occurences(1.0f / (float) differentActions).roots(Lists.newArrayList(nRoot, nRoot2)).build();

			scenarios.add(scenario);
		}

		return ScalabilityMonitoringDataGenerator.builder().childs(scenarios).build();
	}

	private ScalabilityMonitoringDataGenerator createGenerator(List<InternalAction> actions, int actionCount) {
		ResourceDemandingSEFF seff = (ResourceDemandingSEFF) actions.get(0).eContainer();
		List<ScalabilityMonitoringDataGeneratorScenario> scenarios = Lists.newArrayList();

		AbstractMonitoringDataGenerator nRoot = ServiceCallGenerator.builder().hostId("host").hostName("host")
				.serviceIds(Lists.newArrayList(seff.getId()))
				.intervalGenerator(Optional.of(new EasyDistinctIntervalCreator(50000000))).min(1).max(1).build();
		AbstractMonitoringDataGenerator nChild = InternalActionGenerator.builder()
				.intervalGenerator(Optional.of(new EasyDistinctIntervalCreator(50000000))).min(1).max(1)
				.internalActionId(actions.get(0).getId()).resourceId(RESOURCE_ID_CPU).build();
		nRoot.addChild(nChild);

		AbstractMonitoringDataGenerator nRoot2 = ResourceUsageGenerator.builder()
				.intervalGenerator(Optional.of(new EasyDistinctIntervalCreator(50000000))).hostId("host")
				.hostName("host").min(1).max(1).resourceId(RESOURCE_ID_CPU).usageMin(1).usageMax(1).build();

		ScalabilityMonitoringDataGeneratorScenario scenario = ScalabilityMonitoringDataGeneratorScenario.builder()
				.occurences(1.0f).roots(Lists.newArrayList(nRoot, nRoot2)).build();

		scenarios.add(scenario);

		return ScalabilityMonitoringDataGenerator.builder().childs(scenarios).build();
	}

	private static class EasyDistinctIntervalCreator implements StartStopTimeGenerator {

		private long currentStart = 0;
		private long duration;

		private EasyDistinctIntervalCreator(long duration) {
			this.duration = duration;
		}

		@Override
		public Pair<Long, Long> generateNextInterval() {
			Pair<Long, Long> output = Pair.of(currentStart, currentStart + duration);
			currentStart += duration;
			return output;
		}

	}

}
