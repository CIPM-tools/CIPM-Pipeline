package dmodel.pipeline.core.facade.pcm;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import dmodel.pipeline.core.facade.IResettableQueryFacade;

public interface IUsageQueryFacade extends IResettableQueryFacade {

	void setUsageScenarios(List<UsageScenario> data);

	int getScnearioCount();

}
