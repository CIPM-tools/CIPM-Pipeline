package dmodel.pipeline.vsum.facade;

import java.util.Optional;

import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer;

public interface ISpecificVsumFacade extends IVsumFacade {
	// specific for our use case
	public Optional<ResourceContainer> getCorrespondingResourceContainer(RuntimeResourceContainer rrc);

	public Optional<ServiceInstrumentationPoint> getCorrespondingInstrumentationPoint(ServiceEffectSpecification seff);

}
