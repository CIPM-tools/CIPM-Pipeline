package cipm.consistency.tools.evaluation.load;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(basePackages = { "cipm" })
@Configuration
@EnableScheduling
@EnableConfigurationProperties
@EnableWebMvc
public class LoadOrchestratorEntryPoint {
}
