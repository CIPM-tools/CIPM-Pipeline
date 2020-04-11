package dmodel.pipeline.rt.pcm.resourceenv;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.MonitoringDataUtil;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pcm.util.PCMUtils;

// TODO broken because it needs VSUM
public class ResourceEnvironmentDerivationTest {

	private ResourceEnvironmentTransformation transformation;

	@BeforeClass
	public static void setUpPCM() {
		PCMUtils.loadPCMModels();
	}

	@Before
	public void setUp() throws Exception {
		this.transformation = new ResourceEnvironmentTransformation();
		this.transformation.setBlackboard(new RuntimePipelineBlackboard());
	}

	@Test
	public void test() {
		ServiceCallRecord a = new ServiceCallRecord("", "0", "host1", "MBOOK1", "A", "<not set>", "", "", "", 0, 0);
		ServiceCallRecord b = new ServiceCallRecord("", "1", "host2", "MBOOK2", "B", "<not set>", "0", "", "", 0, 0);

		// create list
		List<ServiceCallRecord> l = new ArrayList<>();
		l.add(a);
		l.add(b);

		// create pcm
		InMemoryPCM pcm = new InMemoryPCM();
		pcm.setResourceEnvironmentModel(ResourceenvironmentFactory.eINSTANCE.createResourceEnvironment());

		// trigger it
		transformation.deriveResourceEnvironment(MonitoringDataUtil.buildServiceCallTree(l));

		// check results
		assertEquals(2, pcm.getResourceEnvironmentModel().getResourceContainer_ResourceEnvironment().size());
		assertEquals(1, pcm.getResourceEnvironmentModel().getLinkingResources__ResourceEnvironment().size());
	}

}
