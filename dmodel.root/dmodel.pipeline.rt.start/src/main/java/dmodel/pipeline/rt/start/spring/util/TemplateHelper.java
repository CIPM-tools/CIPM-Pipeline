package dmodel.pipeline.rt.start.spring.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import dmodel.pipeline.shared.json.JsonEObject;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

@Service
public class TemplateHelper {

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@Autowired
	private DModelConfigurationContainer config;

	public void addModelOverviewInformation(Model model) {
		InMemoryPCM pcm = blackboard.getArchitectureModel();

		if (pcm == null) {
			blackboard.loadArchitectureModel(config.getModels());
			pcm = blackboard.getArchitectureModel();
			if (pcm == null) {
				return;
			}
		}

		// system
		model.addAttribute("system", JsonEObject.create(pcm.getSystem()));
		model.addAttribute("system_updated", new Date(pcm.getLastUpdatedSystem()));

		// repository
		model.addAttribute("repository", JsonEObject.create(pcm.getRepository()));
		model.addAttribute("repository_updated", new Date(pcm.getLastUpdatedRepository()));

		// allocation
		model.addAttribute("allocation", JsonEObject.create(pcm.getAllocationModel()));
		model.addAttribute("allocation_updated", new Date(pcm.getLastUpdatedAllocation()));

		// usage
		model.addAttribute("usage", JsonEObject.create(pcm.getUsageModel()));
		model.addAttribute("usage_updated", new Date(pcm.getLastUpdatedUsage()));

		// resource environment
		model.addAttribute("resenv", JsonEObject.create(pcm.getResourceEnvironmentModel()));
		model.addAttribute("resenv_updated", new Date(pcm.getLastUpdatedResourceEnv()));
	}

}
