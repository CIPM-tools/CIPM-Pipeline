package dmodel.pipeline.rt.pipeline.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pipeline.AbstractPipelineTestBase;
import dmodel.pipeline.rt.pipeline.BasePipelineTestConfiguration;
import dmodel.pipeline.shared.structure.Tree;

@RunWith(SpringRunner.class)
@Import(BasePipelineTestConfiguration.TestContextConfiguration.class)
public class CorePipelineFunctionalityTest extends AbstractPipelineTestBase {

	@Test
	public void monitoringSynthesizerTest() {
		List<Tree<ServiceCallRecord>> callTrees = super.parseMonitoringResource("/sample.dat");

		assertEquals(2, callTrees.size());
	}

}
