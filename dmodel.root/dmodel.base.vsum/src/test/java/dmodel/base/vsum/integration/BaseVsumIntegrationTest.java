package dmodel.base.vsum.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.base.core.IPcmModelProvider;
import dmodel.base.core.ISpecificModelProvider;
import dmodel.base.models.runtimeenvironment.REModel.REModelFactory;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.base.vsum.VsumManagerTestBase;
import dmodel.base.vsum.facade.IVsumFacade;
import dmodel.base.vsum.manager.VsumManager;
import dmodel.base.vsum.manager.VsumManager.VsumChangeSource;

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

	@Before
	public void startup() {
		setSpecific(null, null, null); // empty models 2
		setPcm(null, null, null, null, null); // empty models

		this.reloadVsum();
	}

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
