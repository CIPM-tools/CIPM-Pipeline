package dmodel.pipeline.rt.start.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dmodel.pipeline.rt.start.spring.config.ITemplateMapping;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;

@Controller
public class TemplateController {
	@Autowired
	private DModelConfigurationContainer configuration;

	@RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
	public String index(Model model) {
		this.prepareModel(model);
		model.addAttribute("fragment", ITemplateMapping.HOME_FRAGMENT);

		return "index";
	}

	@RequestMapping(value = { "/config/projectprops" }, method = RequestMethod.GET)
	public String configProject(Model model) {
		this.prepareModel(model);
		model.addAttribute("fragment", ITemplateMapping.CONFIG_PROPS_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_PROPS_FRAGMENT_JS);

		return "index";
	}

	@RequestMapping(value = { "/config/modelprops" }, method = RequestMethod.GET)
	public String configModels(Model model) {
		this.prepareModel(model);
		model.addAttribute("fragment", ITemplateMapping.CONFIG_MODELS_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_MODELS_FRAGMENT_JS);

		return "index";
	}

	@RequestMapping(value = { "/design/index", "/design/" }, method = RequestMethod.GET)
	public String designTime(Model model) {
		this.prepareModel(model);
		model.addAttribute("fragment", ITemplateMapping.CONFIG_DESIGNTIME_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_DESIGNTIME_FRAGMENT_JS);

		model.addAttribute("repo_path", configuration.getModels().getRepositoryPath());
		model.addAttribute("system_present", configuration.getModels().getSystemPath() != null
				&& configuration.getModels().getSystemPath().length() > 0);

		return "index";
	}

	@RequestMapping(value = { "/design/buildsys", "/design/build/" }, method = RequestMethod.GET)
	public String designTimeSystemBuilding(Model model) {
		this.prepareModel(model);
		model.addAttribute("fragment", ITemplateMapping.CONFIG_DESIGNTIME_SYSTEM_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_DESIGNTIME_SYSTEM_FRAGMENT_JS);
		model.addAttribute("fragment_footer", ITemplateMapping.CONFIG_DESIGNTIME_SYSTEM_FRAGMENT_FOOTER);

		model.addAttribute("system_present", configuration.getModels().getSystemPath() != null
				&& configuration.getModels().getSystemPath().length() > 0);

		return "index";
	}

	private void prepareModel(Model model) {
		model.addAttribute("runtime_available", true);
	}

}
