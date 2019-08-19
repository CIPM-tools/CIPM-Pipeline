package dmodel.pipeline.rt.start;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dmodel.pipeline.shared.pcm.PCMUtils;

@SpringBootApplication
@ComponentScan(basePackages = "dmodel.pipeline")
@Configuration
@EnableConfigurationProperties
@EnableWebMvc
public class DModelRuntimeStarter implements InitializingBean, WebMvcConfigurer {
	@Override
	public void afterPropertiesSet() throws Exception {
		PCMUtils.loadPCMModels();
	}

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/public/" };

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS)
				.setCacheControl(CacheControl.noCache());
	}
}
