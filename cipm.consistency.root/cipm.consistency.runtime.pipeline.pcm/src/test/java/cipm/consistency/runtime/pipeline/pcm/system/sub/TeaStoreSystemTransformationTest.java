package cipm.consistency.runtime.pipeline.pcm.system.sub;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.core.facade.IRuntimeEnvironmentQueryFacade;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.bridge.monitoring.util.MonitoringDataUtil;
import cipm.consistency.runtime.pipeline.pcm.system.AbstractBaseSystemTransformationTest;
import cipm.consistency.runtime.pipeline.pcm.system.RuntimeSystemTransformation;
import kieker.analysis.exception.AnalysisConfigurationException;

@RunWith(SpringRunner.class)
@Import(AbstractBaseSystemTransformationTest.SystemTransformationTestConfiguration.class)
public class TeaStoreSystemTransformationTest extends AbstractBaseSystemTransformationTest {

	@Autowired
	private IPcmModelProvider pcm;

	@Autowired
	private RuntimeSystemTransformation transformation;

	@Autowired
	private IRuntimeEnvironmentQueryFacade remQuery;

	@Override
	protected void loadModels() {
		setSpecific(null, null, null);
		setPcm(ModelUtil.readFromResource(
				AbstractBaseSystemTransformationTest.class.getResource("/teastore/models/repository_29.repository"),
				Repository.class),
				ModelUtil.readFromResource(
						AbstractBaseSystemTransformationTest.class.getResource("/teastore/models/system_29.system"),
						org.palladiosimulator.pcm.system.System.class),
				ModelUtil.readFromResource(AbstractBaseSystemTransformationTest.class
						.getResource("/teastore/models/resourceenv_29.resourceenvironment"), ResourceEnvironment.class),
				ModelUtil.readFromResource(AbstractBaseSystemTransformationTest.class
						.getResource("/teastore/models/allocation_29.allocation"), Allocation.class),
				null);
	}

	@Override
	@Before
	public void initHostMapping() {
		loadModels();
		super.reloadVsum();

		pcm.getResourceEnvironment().getResourceContainer_ResourceEnvironment().forEach(r -> {
			remQuery.createResourceContainer(r.getEntityName(), r.getEntityName());
		});
	}

	@Test
	public void systemUpdateTest() throws IllegalStateException, AnalysisConfigurationException, URISyntaxException {
		System.out.println(pcm.getRaw().getSystem().getAssemblyContexts__ComposedStructure().size());
		List<PCMContextRecord> dataset = MonitoringDataUtil.getMonitoringDataFromFiles(
				new File(getClass().getResource("/teastore/monitoring").toURI()).getAbsolutePath());

		transformation.transformSystem(
				MonitoringDataUtil.buildServiceCallTree(dataset.stream().filter(r -> r instanceof ServiceCallRecord)
						.map(ServiceCallRecord.class::cast).collect(Collectors.toList())));

		System.out.println(pcm.getRaw().getSystem().getAssemblyContexts__ComposedStructure().size());
		System.out.println("Finished.");

		ModelUtil.saveToFile(pcm.getRaw().getSystem(), "debug.system");
	}

}
