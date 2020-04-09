package dmodel.pipeline.rt.start;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import dmodel.pipeline.core.CentralModelAdminstrator;
import dmodel.pipeline.models.mapping.MappingPackage;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import dmodel.pipeline.shared.correspondence.CorrespondenceUtil;
import dmodel.pipeline.shared.pcm.util.PCMUtils;

@SpringBootApplication
@ComponentScan(basePackages = "dmodel.pipeline")
@Configuration
@EnableScheduling
@EnableConfigurationProperties
@EnableWebMvc
public class DModelRuntimeStarter implements InitializingBean, WebMvcConfigurer, SchedulingConfigurer {
	@Value("${config}")
	private String configPath;

	@Autowired
	private CentralModelAdminstrator modelContainer;

	@Autowired
	private DModelConfigurationContainer config;

	@Override
	public void afterPropertiesSet() throws Exception {
		// load our emf models
		PCMUtils.loadPCMModels();
		CorrespondenceUtil.initVitruv();
		MappingPackage.eINSTANCE.eClass();

		// load models into blackboard
		modelContainer.loadArchitectureModel(config.getModels());
	}

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/public/" };

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS)
				.setCacheControl(CacheControl.noCache());
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

		threadPoolTaskScheduler.setDaemon(true);
		threadPoolTaskScheduler.setPoolSize(2);
		threadPoolTaskScheduler.setThreadNamePrefix("springboot-threadpool");
		threadPoolTaskScheduler.initialize();

		scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
	}

	@Bean
	public DModelConfigurationContainer loadConfiguration() {
		File configFile = new File(configPath);
		if (!configFile.exists()) {
			return null;
		}

		// for a single use - not efficient but sufficient
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();

		try {
			DModelConfigurationContainer output = mapper.readValue(configFile, DModelConfigurationContainer.class);
			output.setFileBackedPath(configFile); // => to save it properly
			return output;
		} catch (IOException e) {
			e.printStackTrace();
			// no valid configuration
			return null;
		}
	}

	@Bean
	public ScheduledExecutorService prepareThreadPool() {
		return Executors.newScheduledThreadPool(5);
	}
}
