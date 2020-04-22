package dmodel.pipeline.core.mocks;

import dmodel.pipeline.core.ISpecificModelProvider;
import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModelFactory;
import dmodel.pipeline.rt.runtimeenvironment.REModel.REModelFactory;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import tools.vitruv.framework.correspondence.CorrespondenceFactory;
import tools.vitruv.framework.correspondence.Correspondences;

public class StaticSpecificModelProviderImpl implements ISpecificModelProvider {
	private InstrumentationModel inm;
	private RuntimeEnvironmentModel rem;
	private Correspondences cps;

	@Override
	public InstrumentationModel getInstrumentation() {
		return inm;
	}

	@Override
	public RuntimeEnvironmentModel getRuntimeEnvironment() {
		return rem;
	}

	@Override
	public Correspondences getCorrespondences() {
		return cps;
	}

	public void setModels(InstrumentationModel inm, RuntimeEnvironmentModel rem, Correspondences cps) {
		this.inm = inm == null ? InstrumentationModelFactory.eINSTANCE.createInstrumentationModel() : inm;
		this.rem = rem == null ? REModelFactory.eINSTANCE.createRuntimeEnvironmentModel() : rem;
		this.cps = cps == null ? CorrespondenceFactory.eINSTANCE.createCorrespondences() : cps;
	}

}
