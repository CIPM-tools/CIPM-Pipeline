package cipm.consistency.runtime.pipeline.pcm.repository.cocome;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Sets;

import cipm.consistency.base.core.facade.IRuntimeEnvironmentQueryFacade;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.util.MonitoringDataUtil;
import cipm.consistency.runtime.pipeline.AbstractPipelineTestBase;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.data.PCMPartionedMonitoringData;
import cipm.consistency.runtime.pipeline.data.PartitionedMonitoringData;
import cipm.consistency.runtime.pipeline.pcm.repository.AbstractRepositoryTransformationTestBase;
import cipm.consistency.runtime.pipeline.pcm.repository.IRepositoryCalibration;
import cipm.consistency.runtime.pipeline.pcm.repository.calibration.RepositoryStoexChanges;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;
import kieker.analysis.exception.AnalysisConfigurationException;

@RunWith(SpringRunner.class)
@Import(AbstractRepositoryTransformationTestBase.RepositoryTransformationTestConfiguration.class)
public class CoCoMERepositoryTransformationTest extends AbstractPipelineTestBase {

	@Autowired
	private IRepositoryCalibration transformation;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private IRuntimeEnvironmentQueryFacade remQuery;

	@Before
	public void loadModels() {
		setSpecific(null, null, null);

		setPcm(ModelUtil.readFromResource(getClass().getResource("/cocome/models/cocome.repository"), Repository.class),
				ModelUtil.readFromResource(getClass().getResource("/cocome/models/cocome.system"), System.class),
				ModelUtil.readFromResource(getClass().getResource("/cocome/models/cocome.resourceenvironment"),
						ResourceEnvironment.class),
				ModelUtil.readFromResource(getClass().getResource("/cocome/models/cocome.allocation"),
						Allocation.class),
				null);

		this.reloadVsum();
		remQuery.createResourceContainer("dde5840d2193ead2f4f35c131453d19d", "cocome-virt");
	}

	@Test
	public void test() throws IllegalStateException, AnalysisConfigurationException, URISyntaxException {
		transformation.reset();
		List<PCMContextRecord> dataset = MonitoringDataUtil.getMonitoringDataFromFiles(
				new File(getClass().getResource("/cocome/monitoring").toURI()).getAbsolutePath());

		PartitionedMonitoringData<PCMContextRecord> pcmData = new PCMPartionedMonitoringData(dataset, 0.1f);

		RepositoryStoexChanges changes = transformation.calibrateRepository(pcmData.getAllData(),
				blackboard.getPcmQuery(), new ValidationData(), Sets.newHashSet());

		assertTrue(changes.size() > 0);
	}

}
