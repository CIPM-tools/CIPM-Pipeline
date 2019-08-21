package dmodel.pipeline.rt.rest.dt;

import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.records.instrument.IApplicationInstrumenter;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.rest.core.processes.ReloadModelsProcess;
import dmodel.pipeline.rt.rest.dt.async.InstrumentationProcess;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import dmodel.pipeline.shared.util.StackedRunnable;

@RestController
public class DesignTimeRestController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DModelConfigurationContainer config;

	@Autowired
	private IApplicationInstrumenter transformer;

	@Autowired
	private ScheduledExecutorService executorService;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@PostMapping("/design/instrument")
	// TODO refactor
	public void instrumentApplication() {
		if (config.getProject().getCorrespondencePath().equals("")) {
			return;
		}

		// create processes
		ReloadModelsProcess process1 = new ReloadModelsProcess(blackboard, config.getModels());
		InstrumentationProcess process2 = new InstrumentationProcess(config.getProject(), transformer);

		// execute them
		executorService.submit(new StackedRunnable(process1, process2));
	}

	@GetMapping("/design/instrument")
	public String instrumentationStatus() {
		return null;
	}

}
