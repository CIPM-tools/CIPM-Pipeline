package cipm.consistency.app.start.spring;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cipm.consistency.app.start.spring.config.ITemplateMapping;
import cipm.consistency.app.start.spring.util.TemplateHelper;
import cipm.consistency.base.core.ICallGraphProvider;
import cipm.consistency.base.core.config.ConfigurationContainer;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.validation.simulation.HeadlessPCMSimulator;

@Controller
public class TemplateController {
	@Autowired
	private ConfigurationContainer configuration;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private HeadlessPCMSimulator simulator;

	@Autowired
	private TemplateHelper helper;

	@Autowired
	private ServerProperties serverProperties;

	@Autowired
	private ICallGraphProvider callGraphProvider;

	@RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
	public String index(Model model) {
		this.prepareModel(model, "overview");
		model.addAttribute("fragment", ITemplateMapping.HOME_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.HOME_FRAGMENT_JS);

		return "index";
	}

	@RequestMapping(value = { "/design/system-extraction" }, method = RequestMethod.GET)
	public String designTimeSystemExtraction(Model model) {
		this.prepareModel(model, "design");
		model.addAttribute("fragment", ITemplateMapping.CONFIG_DESIGNTIME_SYSTEM_EXTRACTION_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_DESIGNTIME_SYSTEM_EXTRACTION_FRAGMENT_JS);

		model.addAttribute("scg_nodes",
				callGraphProvider.callGraphPresent() ? callGraphProvider.provideCallGraph().getNodes().size() : 0);
		model.addAttribute("scg_edges",
				callGraphProvider.callGraphPresent() ? callGraphProvider.provideCallGraph().getEdges().size() : 0);

		return "index";
	}

	@RequestMapping(value = { "/design/scg-extraction" }, method = RequestMethod.GET)
	public String designTimeSCGExtraction(Model model) {
		this.prepareModel(model, "design");
		model.addAttribute("fragment", ITemplateMapping.CONFIG_DESIGNTIME_SCG_EXTRACTION_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_DESIGNTIME_SCG_EXTRACTION_FRAGMENT_JS);

		return "index";
	}

	@RequestMapping(value = { "/design/instrumentation" }, method = RequestMethod.GET)
	public String designTimeInstrumentation(Model model) {
		this.prepareModel(model, "design");
		model.addAttribute("fragment", ITemplateMapping.CONFIG_DESIGNTIME_INSTRUMENTATION_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_DESIGNTIME_INSTRUMENTATION_FRAGMENT_JS);

		try {
			InetAddress localAddress = InetAddress.getLocalHost();
			model.addAttribute("default_inm", "http://" + localAddress.getHostAddress() + ":"
					+ serverProperties.getPort() + "/runtime/pipeline/imm");
		} catch (UnknownHostException e) {
			model.addAttribute("default_inm", "Unknown");
		}

		return "index";
	}

	@RequestMapping(value = { "/design/buildsys", "/design/build/" }, method = RequestMethod.GET)
	public String designTimeSystemBuilding(Model model) {
		this.prepareModel(model, "design");
		model.addAttribute("fragment", ITemplateMapping.CONFIG_DESIGNTIME_SYSTEM_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_DESIGNTIME_SYSTEM_FRAGMENT_JS);

		model.addAttribute("system_present", configuration.getModels().getSystemPath() != null
				&& configuration.getModels().getSystemPath().length() > 0);
		helper.addModelOverviewInformation(model);

		return "index";
	}

	@RequestMapping(value = { "/runtime/", "/runtime/index", "/runtime/overview" }, method = RequestMethod.GET)
	public String runtimeOverview(Model model) {
		this.prepareModel(model, "runtime");
		model.addAttribute("fragment", ITemplateMapping.CONFIG_RUNTIME_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_RUNTIME_FRAGMENT_JS);
		model.addAttribute("fragment_footer", ITemplateMapping.CONFIG_RUNTIME_FRAGMENT_FOOTER);

		return "index";
	}

	@RequestMapping(value = { "/models/summary", "/models/overview" }, method = RequestMethod.GET)
	public String modelOverview(Model model) {
		this.prepareModel(model, "models");
		model.addAttribute("fragment", ITemplateMapping.CONFIG_MODELS_OVERVIEW_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_MODELS_OVERVIEW_FRAGMENT_JS);

		helper.addModelOverviewInformation(model);

		return "index";
	}

	@RequestMapping(value = { "/validation/", "/validation/index" }, method = RequestMethod.GET)
	public String validationData(Model model) {
		this.prepareModel(model, "validation");
		model.addAttribute("fragment", ITemplateMapping.CONFIG_VALIDATION_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_VALIDATION_FRAGMENT_JS);

		return "index";
	}

	private void prepareModel(Model model, String page) {
		model.addAttribute("runtime_available", blackboard.isApplicationRunning());
		model.addAttribute("headless_available", simulator.isReachable());
		model.addAttribute("currentPage", page);
	}

}
