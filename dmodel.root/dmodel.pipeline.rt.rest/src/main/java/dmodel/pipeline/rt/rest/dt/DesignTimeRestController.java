package dmodel.pipeline.rt.rest.dt;

import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.core.config.ConfigurationContainer;
import dmodel.pipeline.records.instrument.IApplicationInstrumenter;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.rest.dt.async.InstrumentationProcess;
import dmodel.pipeline.rt.rest.dt.data.InstrumentationStatus;
import dmodel.pipeline.shared.JsonUtil;

@RestController
public class DesignTimeRestController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ConfigurationContainer config;

	@Autowired
	private IApplicationInstrumenter transformer;

	@Autowired
	private ScheduledExecutorService executorService;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	// current holders
	private InstrumentationStatus instrumentationStatus = InstrumentationStatus.NOT_AVAILABLE;

	@PostMapping("/design/instrument")
	public String instrumentApplication() {
		if (config.getProject().getCorrespondencePath().equals("")) {
			return JsonUtil.emptyObject();
		}

		// create processes
		InstrumentationProcess process = new InstrumentationProcess(config.getProject(), blackboard, transformer);

		// add progress listener
		process.addListener(status -> {
			instrumentationStatus = status;
		});

		// execute them
		executorService.submit(process);

		// no return
		return JsonUtil.emptyObject();
	}

	@GetMapping("/design/instrument/status")
	public String instrumentationStatus() {
		return JsonUtil.wrapAsObject("status", instrumentationStatus.getProgress(), false);
	}

}
