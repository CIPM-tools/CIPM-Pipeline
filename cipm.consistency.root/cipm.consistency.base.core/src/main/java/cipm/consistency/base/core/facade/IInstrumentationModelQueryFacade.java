package cipm.consistency.base.core.facade;

import java.util.Set;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;

/**
 * Facade for accessing the {@link InstrumentationModel} that is currently being
 * used.
 * 
 * @author David Monschein
 *
 */
public interface IInstrumentationModelQueryFacade {

	/**
	 * Collects all services that should be instrumented and monitored fine-grained.
	 * 
	 * @return services that should be instrumented and monitored fine-grained
	 */
	Set<ServiceInstrumentationPoint> getFineGrainedInstrumentedServices();

	/**
	 * Gets the IDs of all actions that should be instrumented and monitored.
	 * 
	 * @return IDs of all actions that should be instrumented and monitored
	 */
	Set<String> getInstrumentedActionIds();

}
