package dmodel.runtime.pipeline.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.base.shared.structure.Tree;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.runtime.pipeline.AbstractPipelineTestBase;
import dmodel.runtime.pipeline.BasePipelineTestConfiguration;

@RunWith(SpringRunner.class)
@Import(BasePipelineTestConfiguration.TestContextConfiguration.class)
public class CorePipelineFunctionalityTest extends AbstractPipelineTestBase {

	@Test
	public void monitoringSynthesizerTest() {
		List<Tree<ServiceCallRecord>> callTrees = super.parseMonitoringResource("/sample.dat");

		assertEquals(2, callTrees.size());
	}

}
