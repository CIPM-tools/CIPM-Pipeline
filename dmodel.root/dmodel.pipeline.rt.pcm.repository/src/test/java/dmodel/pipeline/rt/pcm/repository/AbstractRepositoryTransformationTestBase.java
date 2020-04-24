package dmodel.pipeline.rt.pcm.repository;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.pipeline.rt.pipeline.AbstractPipelineTestBase;
import dmodel.pipeline.rt.pipeline.BasePipelineTestConfiguration;

@RunWith(SpringRunner.class)
public class AbstractRepositoryTransformationTestBase extends AbstractPipelineTestBase {

	@TestConfiguration
	public static class RepositoryTransformationTestConfiguration
			extends BasePipelineTestConfiguration.TestContextConfiguration {

		@Bean
		public RepositoryDerivation transformation() {
			return new RepositoryDerivation();
		}

	}

}
