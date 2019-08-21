package dmodel.pipeline.rt.start.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dmodel.pipeline.rt.start.spring.config.ITemplateMapping;

@Controller
public class TemplateController {

	@RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("fragment", ITemplateMapping.HOME_FRAGMENT);

		return "index";
	}

	@RequestMapping(value = { "/config/projectprops" }, method = RequestMethod.GET)
	public String configProject(Model model) {
		model.addAttribute("fragment", ITemplateMapping.CONFIG_PROPS_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_PROPS_FRAGMENT_JS);

		return "index";
	}

	@RequestMapping(value = { "/config/modelprops" }, method = RequestMethod.GET)
	public String configModels(Model model) {
		model.addAttribute("fragment", ITemplateMapping.CONFIG_MODELS_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_MODELS_FRAGMENT_JS);

		return "index";
	}

	@RequestMapping(value = { "/design/index", "/design/" }, method = RequestMethod.GET)
	public String designTime(Model model) {
		model.addAttribute("fragment", ITemplateMapping.CONFIG_DESIGNTIME_FRAGMENT);
		model.addAttribute("fragment_js", ITemplateMapping.CONFIG_DESIGNTIME_FRAGMENT_JS);

		return "index";
	}

}
