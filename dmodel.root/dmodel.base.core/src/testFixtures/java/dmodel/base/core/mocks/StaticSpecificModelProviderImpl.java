package dmodel.base.core.mocks;

import dmodel.base.core.ISpecificModelProvider;
import dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelFactory;
import dmodel.base.models.runtimeenvironment.REModel.REModelFactory;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel;
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
