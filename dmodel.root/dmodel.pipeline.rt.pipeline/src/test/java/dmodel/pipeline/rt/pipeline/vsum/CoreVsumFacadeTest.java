package dmodel.pipeline.rt.pipeline.vsum;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.core.ISpecificModelProvider;
import dmodel.pipeline.rt.pipeline.AbstractPipelineTestBase;
import dmodel.pipeline.rt.pipeline.BasePipelineTestConfiguration;
import dmodel.pipeline.rt.validation.facade.RuntimeEnvironmentQueryImpl;

@RunWith(SpringRunner.class)
@Import(BasePipelineTestConfiguration.TestContextConfiguration.class)
public class CoreVsumFacadeTest extends AbstractPipelineTestBase {

	@Autowired
	private ISpecificModelProvider specificModelProvider;

	@Autowired
	private IPcmModelProvider pcmModelProvider;

	@Autowired
	private RuntimeEnvironmentQueryImpl remQuery;

	@Test
	public void createContainerTest() {
		remQuery.createResourceContainer("host", "host@abcd");

		assertEquals(1, specificModelProvider.getRuntimeEnvironment().getContainers().size());
		assertEquals(1, pcmModelProvider.getResourceEnvironment().getResourceContainer_ResourceEnvironment().size());
	}

	@Test
	public void interferenceTest() {
		pcmModelProvider.getSystem().getAssemblyContexts__ComposedStructure()
				.add(CompositionFactory.eINSTANCE.createAssemblyContext());

		remQuery.createResourceContainer("host", "host@abcd");

		assertEquals(1, specificModelProvider.getRuntimeEnvironment().getContainers().size());
		assertEquals(1, pcmModelProvider.getResourceEnvironment().getResourceContainer_ResourceEnvironment().size());
	}

	@Test
	public void extendedTest() {
		remQuery.createResourceContainer("host", "host@abcd");
		remQuery.createResourceContainer("host2", "host2@abcd");

		remQuery.createResourceContainerLink("host", "host2");

		assertEquals(2, specificModelProvider.getRuntimeEnvironment().getContainers().size());
		assertEquals(2, pcmModelProvider.getResourceEnvironment().getResourceContainer_ResourceEnvironment().size());
		assertEquals(1, specificModelProvider.getRuntimeEnvironment().getConnections().size());
		assertEquals(1, pcmModelProvider.getResourceEnvironment().getLinkingResources__ResourceEnvironment().size());
	}

}
