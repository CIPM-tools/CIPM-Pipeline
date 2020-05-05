package dmodel.base.core.facade.pcm;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import dmodel.base.core.facade.IResettableQueryFacade;

/**
 * Facade for accessing the underlying usage model ({@link UsageModel}).
 * 
 * @author David Monschein
 *
 */
public interface IUsageQueryFacade extends IResettableQueryFacade {

	/**
	 * Applies a list of usage scenarios to the usage model. Removes all currently
	 * existing usage scenarios.
	 * 
	 * @param data usage scenarios to apply
	 */
	void setUsageScenarios(List<UsageScenario> data);

	/**
	 * Gets the number of usage scenarios within the usage model.
	 * 
	 * @return number of usage scenarios within the usage model
	 */
	int getScnearioCount();

}
