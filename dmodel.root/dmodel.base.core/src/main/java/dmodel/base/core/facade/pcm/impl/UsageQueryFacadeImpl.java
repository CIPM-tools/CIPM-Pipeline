package dmodel.base.core.facade.pcm.impl;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.base.core.IPcmModelProvider;
import dmodel.base.core.facade.pcm.IUsageQueryFacade;

/**
 * Implementation of the {@link IUsageQueryFacade}. Uses no caches, because
 * there is no need.
 * 
 * @author David Monschein
 *
 */
@Component
public class UsageQueryFacadeImpl implements IUsageQueryFacade {
	/**
	 * Provider of the underlying models.
	 */
	@Autowired
	private IPcmModelProvider pcmModelProvider;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset(boolean hard) {
		// nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUsageScenarios(List<UsageScenario> data) {
		if (data.size() > 0) {
			pcmModelProvider.getUsage().getUsageScenario_UsageModel().clear();
			pcmModelProvider.getUsage().getUsageScenario_UsageModel().addAll(data);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getScnearioCount() {
		return pcmModelProvider.getUsage().getUsageScenario_UsageModel().size();
	}
}
