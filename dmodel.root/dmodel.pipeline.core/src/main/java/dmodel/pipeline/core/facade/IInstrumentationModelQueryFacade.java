package dmodel.pipeline.core.facade;

import java.util.Set;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint;

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
