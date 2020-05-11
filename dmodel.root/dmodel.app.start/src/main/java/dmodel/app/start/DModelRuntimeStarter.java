package dmodel.app.start;

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

import dmodel.base.core.config.ConfigurationContainer;
import dmodel.base.core.impl.CentralModelAdminstrator;
import dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage;
import dmodel.base.models.runtimeenvironment.REModel.REModelPackage;
import dmodel.base.shared.pcm.util.PCMUtils;
import dmodel.base.shared.vitruv.VitruviusUtil;
import lombok.extern.java.Log;

@SpringBootApplication
@ComponentScan(basePackages = { "dmodel", "mir.reactions", "mir.routines" })
@Configuration
@EnableScheduling
@EnableConfigurationProperties
@EnableWebMvc
@Log
public class DModelRuntimeStarter implements InitializingBean, WebMvcConfigurer, SchedulingConfigurer {
	@Value("${config}")
	private String configPath;

	@Autowired
	private CentralModelAdminstrator modelContainer;

	@Autowired
	private ConfigurationContainer config;

	@Override
	public void afterPropertiesSet() throws Exception {
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
	public ConfigurationContainer loadConfiguration() {
		// load our emf models
		PCMUtils.loadPCMModels();
		VitruviusUtil.initVitruv();
		InstrumentationModelPackage.eINSTANCE.eClass();
		REModelPackage.eINSTANCE.eClass();

		File configFile = new File(configPath);
		if (!configFile.exists()) {
			log.warning("Configuration file does not exist.");
			log.warning("Path of specified configuration is '" + configPath + "'.");
			return null;
		}

		// for a single use - not efficient but sufficient
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();

		try {
			ConfigurationContainer output = mapper.readValue(configFile, ConfigurationContainer.class);
			output.setFileBackedPath(configFile); // => to save it properly
			return output;
		} catch (IOException e) {
			log.warning("Failed to parse the configuration.");
			log.warning(e.getMessage());
			// no valid configuration
			return null;
		}
	}

	@Bean
	public ScheduledExecutorService prepareThreadPool() {
		return Executors.newScheduledThreadPool(5);
	}
}
