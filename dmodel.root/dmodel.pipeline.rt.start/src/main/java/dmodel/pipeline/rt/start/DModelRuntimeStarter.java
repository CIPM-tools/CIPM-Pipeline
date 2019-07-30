package dmodel.pipeline.rt.start;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import dmodel.pipeline.shared.pcm.PCMUtils;

@SpringBootApplication
@ComponentScan(basePackages = "dmodel.pipeline")
@Configuration
@EnableConfigurationProperties
public class DModelRuntimeStarter implements InitializingBean {
	@Override
	public void afterPropertiesSet() throws Exception {
		PCMUtils.loadPCMModels();
	}
}
