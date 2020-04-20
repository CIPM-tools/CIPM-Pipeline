package dmodel.pipeline.rt.rest.core.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.core.config.ConfigurationContainer;
import dmodel.pipeline.rt.rest.data.config.JsonConceptualConfig;
import dmodel.pipeline.shared.JsonUtil;

@RestController
public class ConfigPropertiesRestController {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ConfigurationContainer config;

	@GetMapping("/config/conceptual/get")
	public String getConceptualConfig() {
		try {
			return objectMapper.writeValueAsString(JsonConceptualConfig.from(config));
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("success", false, false);
		}
	}

	@PostMapping("/config/conceptual/save")
	public String saveConceptualConfig(@RequestParam String config) {
		JsonConceptualConfig parsedConfig;
		try {
			parsedConfig = objectMapper.readValue(config, JsonConceptualConfig.class);
		} catch (IOException e) {
			return JsonUtil.wrapAsObject("success", false, false);
		}

		// apply it
		parsedConfig.applyTo(this.config);

		if (this.config.syncWithFilesystem()) {
			return JsonUtil.wrapAsObject("success", true, false);
		} else {
			return JsonUtil.wrapAsObject("success", false, false);
		}
	}

	@PostMapping("/config/conceptual/validate")
	public String validateConceptualConfig(@RequestParam String config) {
		JsonConceptualConfig parsedConfig;
		try {
			parsedConfig = objectMapper.readValue(config, JsonConceptualConfig.class);
		} catch (IOException e) {
			return JsonUtil.wrapAsObject("success", false, false);
		}

		// use it
		try {
			return objectMapper.writeValueAsString(parsedConfig.validate());
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("success", false, false);
		}
	}

}
