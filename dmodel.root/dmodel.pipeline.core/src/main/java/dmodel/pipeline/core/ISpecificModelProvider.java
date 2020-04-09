package dmodel.pipeline.core;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import tools.vitruv.framework.correspondence.Correspondences;

public interface ISpecificModelProvider {

	InstrumentationModel getInstrumentation();

	RuntimeEnvironmentModel getRuntimeEnvironment();

	Correspondences getCorrespondences();

}
