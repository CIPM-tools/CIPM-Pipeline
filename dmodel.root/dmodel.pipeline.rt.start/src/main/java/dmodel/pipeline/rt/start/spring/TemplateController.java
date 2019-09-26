package dmodel.pipeline.rt.start.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.start.spring.config.ITemplateMapping;
import dmodel.pipeline.rt.start.spring.util.TemplateHelper;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;

@Controller
public class TemplateController {
	@Autowired
	private DModelConfigurationContainer configuration;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private TemplateHelper helper;

	@RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
	public String index(Model model) {
		this.prepareModel(model, "overview");
		model.addAttribute("fragment", ITemplateMapping.HOME_FRAGMENT);

		return "index";
	}

	@RequestMapping(value = { "/config/projectprops" }, method = RequestMethod.GET)
	public String configProject(Model model) {
		this.prepareModel(model, "config");
		model.addAttribute("fragment", ITemplateMapping.CONFIG_PROPS_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_PROPS_FRAGMENT_JS);

		return "index";
	}

	@RequestMapping(value = { "/config/modelprops" }, method = RequestMethod.GET)
	public String configModels(Model model) {
		this.prepareModel(model, "config");
		model.addAttribute("fragment", ITemplateMapping.CONFIG_MODELS_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_MODELS_FRAGMENT_JS);

		return "index";
	}

	@RequestMapping(value = { "/design/index", "/design/" }, method = RequestMethod.GET)
	public String designTime(Model model) {
		this.prepareModel(model, "design");
		model.addAttribute("fragment", ITemplateMapping.CONFIG_DESIGNTIME_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_DESIGNTIME_FRAGMENT_JS);

		model.addAttribute("repo_path", configuration.getModels().getRepositoryPath());
		model.addAttribute("system_present",
				blackboard.getArchitectureModel() != null && blackboard.getArchitectureModel().getSystem() != null
						&& blackboard.getArchitectureModel().getSystem().eContents().size() > 0);

		return "index";
	}

	@RequestMapping(value = { "/design/buildsys", "/design/build/" }, method = RequestMethod.GET)
	public String designTimeSystemBuilding(Model model) {
		this.prepareModel(model, "design");
		model.addAttribute("fragment", ITemplateMapping.CONFIG_DESIGNTIME_SYSTEM_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_DESIGNTIME_SYSTEM_FRAGMENT_JS);
		model.addAttribute("fragment_footer", ITemplateMapping.CONFIG_DESIGNTIME_SYSTEM_FRAGMENT_FOOTER);

		model.addAttribute("system_present", configuration.getModels().getSystemPath() != null
				&& configuration.getModels().getSystemPath().length() > 0);

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

		model.addAttribute("repo_path", configuration.getModels().getRepositoryPath());
		helper.addModelOverviewInformation(model);

		return "index";
	}

	private void prepareModel(Model model, String page) {
		model.addAttribute("runtime_available", blackboard.isApplicationRunning());
		model.addAttribute("currentPage", page);
	}

}
