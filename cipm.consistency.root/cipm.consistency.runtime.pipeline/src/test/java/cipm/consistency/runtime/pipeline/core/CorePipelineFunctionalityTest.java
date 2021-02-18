package cipm.consistency.runtime.pipeline.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.runtime.pipeline.AbstractPipelineTestBase;
import cipm.consistency.runtime.pipeline.BasePipelineTestConfiguration;

@RunWith(SpringRunner.class)
@Import(BasePipelineTestConfiguration.TestContextConfiguration.class)
public class CorePipelineFunctionalityTest extends AbstractPipelineTestBase {

	@Test
	public void monitoringSynthesizerTest() {
		List<Tree<ServiceCallRecord>> callTrees = super.parseMonitoringResource("/sample.dat");

		assertEquals(2, callTrees.size());
	}

}
