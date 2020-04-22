package dmodel.pipeline.rt.rest.dt;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.core.health.HealthState;
import dmodel.pipeline.core.health.HealthStateManager;
import dmodel.pipeline.core.health.HealthStateObservedComponent;
import dmodel.pipeline.instrumentation.manager.InstrumentationManager;
import dmodel.pipeline.instrumentation.manager.ProjectManager;
import dmodel.pipeline.instrumentation.mapping.IAutomatedMappingResolver;
import dmodel.pipeline.rt.rest.dt.async.InstrumentationProcess;
import dmodel.pipeline.rt.rest.dt.data.InstrumentationStatus;
import dmodel.pipeline.rt.rest.dt.data.JsonInstrumentationConfiguration;
import dmodel.pipeline.rt.rest.dt.data.JsonInstrumentationConfigurationValidation;
import dmodel.pipeline.shared.JsonUtil;
import dmodel.pipeline.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.pipeline.vsum.manager.VsumManager;
import lombok.extern.java.Log;

@RestController
@Log
public class DesignTimeRestController {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private InstrumentationManager transformer;

	@Autowired
	private ScheduledExecutorService executorService;

	@Autowired
	private IAutomatedMappingResolver mappingResolver;

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private VsumManager vsumManager;

	@Autowired
	private HealthStateManager healthStateManager;

	// current holders
	private InstrumentationStatus instrumentationStatus = InstrumentationStatus.NOT_AVAILABLE;

	@PostMapping("/design/mapping/resolve")
	public String resolveMappingFromCode() {
		if (healthStateManager.getState(HealthStateObservedComponent.CONFIGURATION) != HealthState.WORKING) {
			return JsonUtil.wrapAsObject("success", false, false);
		}

		mappingResolver.resolveMappings(projectManager.getParsedApplicationProject(),
				vsumManager.getJavaCorrespondences());

		IJavaPCMCorrespondenceModel cpModel = vsumManager.getJavaCorrespondences();
		int mappingAmount = cpModel.getBranchCorrespondences().size() + cpModel.getExternalCallCorrespondences().size()
				+ cpModel.getInternalActionCorrespondences().size() + cpModel.getLoopCorrespondences().size()
				+ cpModel.getSeffCorrespondences().size();

		return JsonUtil.wrapAsObject("mappings", mappingAmount, false);
	}

	@PostMapping("/design/instrument")
	public String instrumentApplication(@RequestParam String config) {
		// parse configuration
		JsonInstrumentationConfiguration parsedConfiguration;
		try {
			parsedConfiguration = objectMapper.readValue(config, JsonInstrumentationConfiguration.class);

			// create processes
			InstrumentationProcess process = new InstrumentationProcess(transformer, parsedConfiguration);

			// add progress listener
			process.addListener(status -> {
				instrumentationStatus = status;
			});

			// execute them
			executorService.submit(process);

			// no return
			return JsonUtil.wrapAsObject("success", true, false);
		} catch (IOException e) {
			log.warning("Failed to parse instrumentation configuration at endpoint '/design/instrument'.");
			return JsonUtil.wrapAsObject("success", false, false);
		}
	}

	@PostMapping("/design/instrument/validate")
	public String validateInstrumentationSettings(@RequestParam String config) {
		// parse configuration
		JsonInstrumentationConfiguration parsedConfiguration;
		try {
			parsedConfiguration = objectMapper.readValue(config, JsonInstrumentationConfiguration.class);

			return objectMapper
					.writeValueAsString(JsonInstrumentationConfigurationValidation.from(parsedConfiguration));
		} catch (IOException e) {
			return JsonUtil.wrapAsObject("valid", false, false);
		}
	}

	@GetMapping("/design/instrument/status")
	public String instrumentationStatus() {
		return JsonUtil.wrapAsObject("status", instrumentationStatus.getProgress(), false);
	}

}
