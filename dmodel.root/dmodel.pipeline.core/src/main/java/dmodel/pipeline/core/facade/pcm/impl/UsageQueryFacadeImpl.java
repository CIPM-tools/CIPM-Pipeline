package dmodel.pipeline.core.facade.pcm.impl;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.springframework.beans.factory.annotation.Autowired;

import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.core.facade.pcm.IUsageQueryFacade;

public class UsageQueryFacadeImpl implements IUsageQueryFacade {
	@Autowired
	private IPcmModelProvider pcmModelProvider;

	@Override
	public void reset(boolean hard) {
		// nothing to do
	}

	@Override
	public void setUsageScenarios(List<UsageScenario> data) {
		if (data.size() > 0) {
			pcmModelProvider.getUsage().getUsageScenario_UsageModel().clear();
			pcmModelProvider.getUsage().getUsageScenario_UsageModel().addAll(data);
		}
	}

	@Override
	public int getScnearioCount() {
		return pcmModelProvider.getUsage().getUsageScenario_UsageModel().size();
	}
}
