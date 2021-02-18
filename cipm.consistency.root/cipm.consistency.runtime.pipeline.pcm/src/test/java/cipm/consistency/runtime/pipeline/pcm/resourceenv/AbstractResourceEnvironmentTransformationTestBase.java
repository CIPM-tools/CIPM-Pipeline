package cipm.consistency.runtime.pipeline.pcm.resourceenv;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import cipm.consistency.runtime.pipeline.AbstractPipelineTestBase;
import cipm.consistency.runtime.pipeline.BasePipelineTestConfiguration;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;

@RunWith(SpringRunner.class)
public abstract class AbstractResourceEnvironmentTransformationTestBase extends AbstractPipelineTestBase {
	@Autowired
	private ResourceEnvironmentTransformation transformation;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@TestConfiguration
	public static class ResourceEnvironmentTransformationTestConfiguration
			extends BasePipelineTestConfiguration.TestContextConfiguration {

		@Bean
		public ResourceEnvironmentTransformation transformation() {
			return new ResourceEnvironmentTransformation();
		}

	}

	@Before
	public void initTransformation() {
		transformation.setBlackboard(blackboard);
	}

}
