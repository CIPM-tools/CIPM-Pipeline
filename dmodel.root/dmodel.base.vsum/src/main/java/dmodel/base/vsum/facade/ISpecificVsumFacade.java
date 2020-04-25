package dmodel.base.vsum.facade;

import java.util.Optional;

import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

import dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;

public interface ISpecificVsumFacade extends IVsumFacade {
	// specific for our use case
	public Optional<ResourceContainer> getCorrespondingResourceContainer(RuntimeResourceContainer rrc);

	public Optional<ServiceInstrumentationPoint> getCorrespondingInstrumentationPoint(ServiceEffectSpecification seff);

}
