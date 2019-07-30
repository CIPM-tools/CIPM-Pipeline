package dmodel.pipeline.rt.start;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import dmodel.pipeline.shared.pcm.PCMUtils;

@SpringBootApplication
@ComponentScan(basePackages = "dmodel.pipeline")
@Configuration
@PropertySource("file:${dmodel_home}/config/external/application.yml")
public class DModelRuntimeStarter implements InitializingBean {
	@Override
	public void afterPropertiesSet() throws Exception {
		PCMUtils.loadPCMModels();
	}
}
