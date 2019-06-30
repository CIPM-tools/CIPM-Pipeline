package dmodel.pipeline.rt.start;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan(basePackages = "dmodel.pipeline")
@Configuration
public class DModelRuntimeStarter {
}
