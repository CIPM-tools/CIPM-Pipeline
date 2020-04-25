package dmodel.runtime.pipeline.pcm.repository;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import dmodel.runtime.pipeline.AbstractPipelineTestBase;
import dmodel.runtime.pipeline.BasePipelineTestConfiguration;
import dmodel.runtime.pipeline.pcm.repository.RepositoryDerivation;

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
