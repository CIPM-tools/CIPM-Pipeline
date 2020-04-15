package dmodel.pipeline.vsum.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.core.ISpecificModelProvider;
import dmodel.pipeline.rt.runtimeenvironment.REModel.REModelFactory;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.pipeline.vsum.VsumManagerTestBase;
import dmodel.pipeline.vsum.facade.IVsumFacade;
import dmodel.pipeline.vsum.manager.VsumManager;
import dmodel.pipeline.vsum.manager.VsumManager.VsumChangeSource;

@RunWith(SpringRunner.class)
@Import(VsumManagerTestBase.VsumManagerTestConfiguration.class)
public class BaseVsumIntegrationTest extends VsumManagerTestBase {

	@Autowired
	private VsumManager vsumManager;

	@Autowired
	private IVsumFacade vsumFacade;

	@Autowired
	private ISpecificModelProvider specificModelProvider;

	@Autowired
	private IPcmModelProvider pcmModelProvider;

	@Test
	public void createRemContainerTest() {
		// the transaction functionality is covered by the reaction tests
		// the target here is to check whether the facades work
		RuntimeResourceContainer newContainer = REModelFactory.eINSTANCE.createRuntimeResourceContainer();
		newContainer.setHostID("test");
		newContainer.setHostname("test@abc");

		vsumManager.executeTransaction(() -> {
			specificModelProvider.getRuntimeEnvironment().getContainers().add(newContainer);
			vsumFacade.createdObject(newContainer, VsumChangeSource.RUNTIME_ENVIRONMENT);
			return null;
		});

		assertEquals(1, pcmModelProvider.getResourceEnvironment().getResourceContainer_ResourceEnvironment().size());
		assertEquals(1, specificModelProvider.getRuntimeEnvironment().getContainers().size());
	}

}
