package dmodel.pipeline.core;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import tools.vitruv.framework.correspondence.Correspondences;

/**
 * Interface for providing the model types that are used by this approach in
 * addition to the classic PCM models.
 * 
 * @author David Monschein
 *
 */
public interface ISpecificModelProvider {

	/**
	 * Gets the instrumentation model.
	 * 
	 * @return instrumentation model
	 */
	InstrumentationModel getInstrumentation();

	/**
	 * Gets the current runtime environment model.
	 * 
	 * @return runtime environment model (REM)
	 */
	RuntimeEnvironmentModel getRuntimeEnvironment();

	/**
	 * Gets the correspondences (mapping) between the model elements.
	 * 
	 * @return correspondences (mapping)
	 */
	Correspondences getCorrespondences();

}
