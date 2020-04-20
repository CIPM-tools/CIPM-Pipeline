package dmodel.pipeline.vsum.facade;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.pipeline.vsum.manager.VsumManager;
import dmodel.pipeline.vsum.manager.VsumManager.VsumChangeSource;
import dmodel.pipeline.vsum.mapping.VsumMappingController;
import tools.vitruv.framework.change.echange.eobject.CreateEObject;
import tools.vitruv.framework.change.echange.eobject.DeleteEObject;

@Component
public class CentralVsumFacade implements ISpecificVsumFacade {
	@Autowired
	private VsumManager manager;

	@Autowired
	private VsumMappingController mapping;

	@Override
	public <T> Optional<T> resolveGenericCorrespondence(EObject obj, String tag, Class<T> type) {
		return mapping.getCorrespondingElement(obj, tag, type);
	}

	@Override
	public <T> List<T> resolveGenericCorrespondences(EObject obj, String tag, Class<T> type) {
		return mapping.getCorrespondingElements(obj, tag, type);
	}

	@Override
	public <T extends EObject> void createdObject(T obj, VsumChangeSource source) {
		manager.executeTransaction(() -> {
			CreateEObject<T> change = manager.getAtomicFactory().createCreateEObjectChange(obj);
			change.setAffectedEObject(obj);
			manager.propagateChange(change, source);

			return null;
		});
	}

	@Override
	public <T extends EObject> void deletedObject(T obj, VsumChangeSource source) {
		manager.executeTransaction(() -> {
			DeleteEObject<T> change = manager.getAtomicFactory().createDeleteEObjectChange(obj);
			change.setAffectedEObject(obj);
			manager.propagateChange(change, source);

			return null;
		});
	}

	@Override
	public Optional<ResourceContainer> getCorrespondingResourceContainer(RuntimeResourceContainer rrc) {
		return mapping.getCorrespondingElement(rrc, ResourceContainer.class);
	}

	@Override
	public Optional<ServiceInstrumentationPoint> getCorrespondingInstrumentationPoint(ServiceEffectSpecification seff) {
		return mapping.getCorrespondingElement(seff, ServiceInstrumentationPoint.class);
	}

}
