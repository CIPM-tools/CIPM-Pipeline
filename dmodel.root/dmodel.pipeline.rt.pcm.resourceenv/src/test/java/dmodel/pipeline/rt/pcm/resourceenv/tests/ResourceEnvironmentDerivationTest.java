package dmodel.pipeline.rt.pcm.resourceenv.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.MonitoringDataUtil;
import dmodel.pipeline.rt.pcm.resourceenv.AbstractResourceEnvironmentTransformationTestBase;
import dmodel.pipeline.rt.pcm.resourceenv.ResourceEnvironmentTransformation;

@RunWith(SpringRunner.class)
@Import(AbstractResourceEnvironmentTransformationTestBase.ResourceEnvironmentTransformationTestConfiguration.class)
public class ResourceEnvironmentDerivationTest extends AbstractResourceEnvironmentTransformationTestBase {
	@Autowired
	private ResourceEnvironmentTransformation transformation;

	@Autowired
	private IPcmModelProvider pcm;

	@Test
	public void test() {
		ServiceCallRecord a = new ServiceCallRecord("", "0", "host1", "MBOOK1", "A", "<not set>", "", "", "", 0, 0);
		ServiceCallRecord b = new ServiceCallRecord("", "1", "host2", "MBOOK2", "B", "<not set>", "0", "", "", 0, 0);

		// create list
		List<ServiceCallRecord> l = new ArrayList<>();
		l.add(a);
		l.add(b);

		// trigger it
		transformation.deriveResourceEnvironment(MonitoringDataUtil.buildServiceCallTree(l));

		// check results
		assertEquals(2, pcm.getResourceEnvironment().getResourceContainer_ResourceEnvironment().size());
		assertEquals(1, pcm.getResourceEnvironment().getLinkingResources__ResourceEnvironment().size());
	}

}
