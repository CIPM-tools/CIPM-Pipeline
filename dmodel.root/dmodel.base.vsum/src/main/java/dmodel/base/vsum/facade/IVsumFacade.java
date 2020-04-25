package dmodel.base.vsum.facade;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import dmodel.base.vsum.manager.VsumManager.VsumChangeSource;

public interface IVsumFacade {
	// correspondences
	public <T> Optional<T> resolveGenericCorrespondence(EObject obj, String tag, Class<T> type);

	public <T> List<T> resolveGenericCorrespondences(EObject obj, String tag, Class<T> type);

	// propagations
	public <T extends EObject> void createdObject(T obj, VsumChangeSource source);

	public <T extends EObject> void deletedObject(T obj, VsumChangeSource source);

}
