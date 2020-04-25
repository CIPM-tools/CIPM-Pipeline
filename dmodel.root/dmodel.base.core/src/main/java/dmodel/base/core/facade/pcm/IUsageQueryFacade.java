package dmodel.base.core.facade.pcm;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import dmodel.base.core.facade.IResettableQueryFacade;

public interface IUsageQueryFacade extends IResettableQueryFacade {

	void setUsageScenarios(List<UsageScenario> data);

	int getScnearioCount();

}
