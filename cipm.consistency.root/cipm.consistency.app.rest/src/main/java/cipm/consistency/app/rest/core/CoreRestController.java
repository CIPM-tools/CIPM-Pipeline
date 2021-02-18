package cipm.consistency.app.rest.core;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class CoreRestController {

	@GetMapping("/core/ping")
	public String ping() {
		return "true";
	}

	@Bean
	public ObjectMapper jsonMapper() {
		return new ObjectMapper();
	}

}
