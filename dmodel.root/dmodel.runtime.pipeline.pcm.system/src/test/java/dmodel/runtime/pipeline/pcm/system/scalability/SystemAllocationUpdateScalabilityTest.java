package dmodel.runtime.pipeline.pcm.system.scalability;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

import dmodel.base.core.IPcmModelProvider;
import dmodel.base.core.facade.IRuntimeEnvironmentQueryFacade;
import dmodel.base.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.base.shared.structure.Tree;
import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.designtime.monitoring.util.MonitoringDataUtil;
import dmodel.runtime.pipeline.AbstractScalabilityTestBase;
import dmodel.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.runtime.pipeline.pcm.system.AbstractBaseSystemTransformationTest;
import dmodel.runtime.pipeline.pcm.system.RuntimeSystemTransformation;
import dmodel.runtime.pipeline.scalability.ScalabilityMonitoringDataGenerator;
import dmodel.runtime.pipeline.scalability.ScalabilityMonitoringDataGeneratorScenario;
import dmodel.runtime.pipeline.scalability.generator.AbstractMonitoringDataGenerator;
import dmodel.runtime.pipeline.scalability.generator.ServiceCallGenerator;
import lombok.extern.java.Log;

@RunWith(SpringRunner.class)
@Log
@Import(AbstractBaseSystemTransformationTest.SystemTransformationTestConfiguration.class)
public class SystemAllocationUpdateScalabilityTest extends AbstractScalabilityTestBase {
	private static final int COMPONENT_CHANGES = 10;
	private static final int RESOURCEENV_SIZE = 50;

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
		Map<Integer, Long> combinedMap = Maps.newHashMap();

		for (int i = 1; i <= COMPONENT_CHANGES; i++) {
			System.out.println(i);
			this.resetSystemAndAllocation();

			ScalabilityMonitoringDataGenerator generator = createGenerator(i);
			Map<Integer, Long> stats = generateScabailityData(generator, generator.getChilds().size(),
					generator.getChilds().size(), 1);
			combinedMap.put(i * RESOURCEENV_SIZE, stats.get(generator.getChilds().size()));
		}

		createPlot(combinedMap, "test-results/scalability/scalability_system_allocation.png",
				"Max component changes at runtime", "Amount of component changes in one iteration",
				"Execution time in seconds", 1);
		saveRawData(combinedMap, "test-results/scalability/scalability_system_allocation_raw.json");
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

		dmodel.base.shared.ModelUtil.saveToFile(pcmModelProvider.getRepository(), "test.repository");

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
