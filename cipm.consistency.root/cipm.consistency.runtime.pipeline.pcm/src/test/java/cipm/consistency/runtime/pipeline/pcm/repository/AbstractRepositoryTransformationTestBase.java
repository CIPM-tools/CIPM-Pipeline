package cipm.consistency.runtime.pipeline.pcm.repository;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import cipm.consistency.runtime.pipeline.BasePipelineTestConfiguration;
import cipm.consistency.runtime.pipeline.pcm.repository.calibration.impl.RepositoryCalibrationImpl;

public class AbstractRepositoryTransformationTestBase {

	@TestConfiguration
	public static class RepositoryTransformationTestConfiguration
			extends BasePipelineTestConfiguration.TestContextConfiguration {

		@Bean
		public IRepositoryCalibration transformation() {
			return new RepositoryCalibrationImpl();
		}

	}

}
