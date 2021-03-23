package cipm.consistency.tools.evaluation.scalability.system;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.core.facade.IRuntimeEnvironmentQueryFacade;
import cipm.consistency.base.core.facade.pcm.IRepositoryQueryFacade;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.bridge.monitoring.util.MonitoringDataUtil;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.pcm.system.AbstractBaseSystemTransformationTest;
import cipm.consistency.runtime.pipeline.pcm.system.RuntimeSystemTransformation;
import cipm.consistency.tools.evaluation.scalability.AbstractScalabilityTestBase;
import cipm.consistency.tools.evaluation.scalability.ScalabilityMonitoringDataGenerator;
import cipm.consistency.tools.evaluation.scalability.ScalabilityMonitoringDataGeneratorScenario;
import cipm.consistency.tools.evaluation.scalability.generator.AbstractMonitoringDataGenerator;
import cipm.consistency.tools.evaluation.scalability.generator.ServiceCallGenerator;
import lombok.extern.java.Log;

@RunWith(SpringRunner.class)
@Log
@Import(AbstractBaseSystemTransformationTest.SystemTransformationTestConfiguration.class)
public class SystemAllocationUpdateScalabilityTest extends AbstractScalabilityTestBase {
	private static final int MIN_COMPONENT_CHANGES = 1;
	private static final int COMPONENT_CHANGES = 80;
	private static final int RESOURCEENV_SIZE = 50;
	private static final int COMPONENT_CHANGES_STEP = 5;
	private static final int REPETITIONS = 10;

	@Autowired
	private RuntimeSystemTransformation transformation;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private IRuntimeEnvironmentQueryFacade remQuery;

	@Autowired
	private IPcmModelProvider pcmModelProvider;

	@Autowired
	private IRepositoryQueryFacade repositoryQuery;

	private static Map<Integer, List<BasicComponent>> idToComponentList;

	@BeforeClass
	public static void initComponentList() {
		Logger.getRootLogger().setLevel(Level.INFO);
		idToComponentList = Maps.newHashMap();
	}

	@Before
	public void initTransformation() {
		transformation.setBlackboard(blackboard);
	}

	@Before
	public void resetBefore() {
		this.resetAll();

		blackboard.reset(true);
	}

	@Test
	public void systemAllocationScalabilityTest() {

		Map<Integer, Long> overallMap = Maps.newHashMap();
		for (int j = 0; j < REPETITIONS; j++) {
			Map<Integer, Long> combinedMap = Maps.newHashMap();

			for (int i = MIN_COMPONENT_CHANGES; i <= COMPONENT_CHANGES; i += COMPONENT_CHANGES_STEP) {
				System.out.println(i);
				this.resetSystemAndAllocation();

				ScalabilityMonitoringDataGenerator generator = createGenerator(i);
				Map<Integer, Long> stats = generateScabailityData(generator, generator.getChilds().size(),
						generator.getChilds().size(), 1);
				combinedMap.put(i * RESOURCEENV_SIZE, stats.get(generator.getChilds().size()));
			}
			overallMap = combineMapsMovingAverage(overallMap, combinedMap);
		}

		createPlot(overallMap, "test-results/scalability/scalability_system_allocation.png",
				"Max component changes at runtime", "Amount of component changes in one iteration",
				"Execution time in seconds", 1);
		saveRawData(overallMap, "test-results/scalability/scalability_system_allocation_raw.json");
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

	private void resetAll() {
		this.resetRepositoryAndEnvironment();
		this.resetSystemAndAllocation();
	}

	private void resetRepositoryAndEnvironment() {
		super.setSpecific(null, null, null);
		super.setPcm(null, null, null, null, null);

		// build system component
		buildComponent(0, COMPONENT_CHANGES);
		// create additional components
		for (int i = 1; i < RESOURCEENV_SIZE; i++) {
			buildComponent(i, COMPONENT_CHANGES);
		}

		// add it to the system
		OperationProvidedRole pOuter = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		pOuter.setProvidedInterface__OperationProvidedRole(repositoryQuery.getOperationInterface("i0"));
		pcmModelProvider.getSystem().getProvidedRoles_InterfaceProvidingEntity().add(pOuter);

		ModelUtil.saveToFile(pcmModelProvider.getRepository(), "test.repository");

		super.reloadVsum();

		// create resource environment
		for (int i = 0; i < RESOURCEENV_SIZE; i++) {
			remQuery.createResourceContainer("h" + i, "h" + i);
		}
		for (int i = 0; i < RESOURCEENV_SIZE - 1; i++) {
			remQuery.createResourceContainerLink("h" + i, "h" + (i + 1));
		}
	}

	private void resetSystemAndAllocation() {
		pcmModelProvider.getAllocation().getAllocationContexts_Allocation().clear();
		pcmModelProvider.getSystem().getAssemblyContexts__ComposedStructure().clear();
		pcmModelProvider.getSystem().getConnectors__ComposedStructure().clear();
	}

	private void buildComponent(int id, int inner) {
		OperationInterface nIface = RepositoryFactory.eINSTANCE.createOperationInterface();
		nIface.setId("i" + id);

		OperationSignature sig = RepositoryFactory.eINSTANCE.createOperationSignature();
		sig.setId("sig" + id);
		nIface.getSignatures__OperationInterface().add(sig);

		pcmModelProvider.getRepository().getInterfaces__Repository().add(nIface);

		List<BasicComponent> comps = Lists.newArrayList();
		for (int i = 0; i < inner; i++) {
			BasicComponent bc = RepositoryFactory.eINSTANCE.createBasicComponent();
			bc.setId("b" + id + "_" + i);
			OperationProvidedRole providedRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
			providedRole.setProvidedInterface__OperationProvidedRole(nIface);
			bc.getProvidedRoles_InterfaceProvidingEntity().add(providedRole);

			ResourceDemandingSEFF belongingService = SeffFactory.eINSTANCE.createResourceDemandingSEFF();
			belongingService.setDescribedService__SEFF(sig);

			// add external call
			ExternalCallAction nExt = SeffFactory.eINSTANCE.createExternalCallAction();
			nExt.setId("e" + id + "_" + i);
			belongingService.getSteps_Behaviour().add(nExt);

			bc.getServiceEffectSpecifications__BasicComponent().add(belongingService);

			belongingService.setId("s" + id + "_" + i);

			pcmModelProvider.getRepository().getComponents__Repository().add(bc);

			comps.add(bc);
		}

		// add required roles
		if (id > 0) {
			idToComponentList.get(id - 1).forEach(comp -> {
				OperationRequiredRole nReq = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
				nReq.setRequiredInterface__OperationRequiredRole(nIface);
				comp.getRequiredRoles_InterfaceRequiringEntity().add(nReq);

				((ExternalCallAction) ((ResourceDemandingSEFF) comp.getServiceEffectSpecifications__BasicComponent()
						.get(0)).getSteps_Behaviour().get(0)).setRole_ExternalService(nReq);
			});
		}

		idToComponentList.put(id, comps);
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

			transformation.transformSystem(tree);

			long after = java.lang.System.nanoTime();

			results.put(i, after - before);
		}

		return results;
	}

	private ScalabilityMonitoringDataGenerator createGenerator(int componentChanges) {
		List<ScalabilityMonitoringDataGeneratorScenario> scenarios = Lists.newArrayList();
		for (int i = 0; i < RESOURCEENV_SIZE - 1; i++) {
			for (int k = 0; k < componentChanges; k++) {
				AbstractMonitoringDataGenerator nRoot = ServiceCallGenerator.builder().hostId("h" + i).hostName("h" + i)
						.serviceIds(Lists.newArrayList("s" + i + "_" + k)).min(1).max(1).build();
				AbstractMonitoringDataGenerator nChild = ServiceCallGenerator.builder().hostId("h" + (i + 1))
						.externalCallId(Optional.of("e" + i + "_" + k)).hostName("h" + (i + 1))
						.serviceIds(Lists.newArrayList("s" + (i + 1) + "_" + k)).min(1).max(1).build();
				nRoot.addChild(nChild);

				ScalabilityMonitoringDataGeneratorScenario scenario = ScalabilityMonitoringDataGeneratorScenario
						.builder().occurences(1.0f / (float) (RESOURCEENV_SIZE * componentChanges))
						.roots(Lists.newArrayList(nRoot)).build();

				scenarios.add(scenario);
			}
		}

		return ScalabilityMonitoringDataGenerator.builder().childs(scenarios).build();
	}

}
