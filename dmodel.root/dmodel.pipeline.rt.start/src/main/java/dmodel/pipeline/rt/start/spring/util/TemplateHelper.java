package dmodel.pipeline.rt.start.spring.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.shared.json.JsonEObject;

@Service
public class TemplateHelper {
	@Autowired
	private IPcmModelProvider modelContainer;

	public void addModelOverviewInformation(Model model) {
		// system
		model.addAttribute("system", JsonEObject.create(modelContainer.getSystem()));
		model.addAttribute("system_updated", new Date());

		// repository
		model.addAttribute("repository", JsonEObject.create(modelContainer.getRepository()));
		model.addAttribute("repository_updated", new Date());

		// allocation
		model.addAttribute("allocation", JsonEObject.create(modelContainer.getAllocation()));
		model.addAttribute("allocation_updated", new Date());

		// usage
		model.addAttribute("usage", JsonEObject.create(modelContainer.getUsage()));
		model.addAttribute("usage_updated", new Date());

		// resource environment
		model.addAttribute("resenv", JsonEObject.create(modelContainer.getResourceEnvironment()));
		model.addAttribute("resenv_updated", new Date());
	}

}
