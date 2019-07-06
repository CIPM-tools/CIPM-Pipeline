package dmodel.pipeline.rt.pcm.resourceenv;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.MonitoringDataUtil;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;

public class ResourceEnvironmentDerivationTest {

	private ResourceEnvironmentDerivation transformation;

	@Before
	public void setUp() throws Exception {
		this.transformation = new ResourceEnvironmentDerivation();
		this.transformation.setBlackboard(new RuntimePipelineBlackboard());
	}

	@Test
	public void test() {
		ServiceCallRecord a = new ServiceCallRecord("", "0", "host1", "A", "<not set>", "", "", 0, 0);
		ServiceCallRecord b = new ServiceCallRecord("", "1", "host2", "B", "<not set>", "0", "", 0, 0);

		// create list
		List<ServiceCallRecord> l = new ArrayList<>();
		l.add(a);
		l.add(b);

		// trigger it
		transformation.deriveResourceEnvironmentData(MonitoringDataUtil.buildServiceCallTree(l));

		// check results
		assertEquals(2,
				transformation.getBlackboard().getMeasurementModel().getEnvironmentData().getHostNames().size());
		assertEquals(1,
				transformation.getBlackboard().getMeasurementModel().getEnvironmentData().getConnections().size());
	}

}
