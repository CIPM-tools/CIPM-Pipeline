package dmodel.pipeline.shared.pcm;

import java.net.URL;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.pcm.PcmPackage;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.resourcetype.ResourcetypePackage;

import de.uka.ipd.sdq.identifier.Identifier;

public class PCMUtils {

	/**
	 * Visits all common PCM package classes to load them.
	 */
	public static void loadPCMModels() {
		RepositoryPackage.eINSTANCE.eClass();
		PcmPackage.eINSTANCE.eClass();
		ResourcetypePackage.eINSTANCE.eClass();

		initPathmaps();
	}

	public static Optional<RequiredRole> getRequiredRoleBySignature(BasicComponent comp, Signature toSig) {
		return comp.getRequiredRoles_InterfaceRequiringEntity().stream().filter(r -> {
			if (r instanceof OperationRequiredRole) {
				return ((OperationRequiredRole) r).getRequiredInterface__OperationRequiredRole()
						.getSignatures__OperationInterface().parallelStream()
						.anyMatch(sig -> sig.getId().equals(toSig.getId()));
			}
			return false;
		}).findFirst();
	}

	public static Optional<ProvidedRole> getProvidedRoleBySignature(BasicComponent comp, Signature toSig) {
		return comp.getProvidedRoles_InterfaceProvidingEntity().stream().filter(r -> {
			if (r instanceof OperationProvidedRole) {
				return ((OperationRequiredRole) r).getRequiredInterface__OperationRequiredRole()
						.getSignatures__OperationInterface().parallelStream()
						.anyMatch(sig -> sig.getId().equals(toSig.getId()));
			}
			return false;
		}).findFirst();
	}

	@SuppressWarnings("unchecked") // it IS a type safe cast
	public static <T> T getElementById(EObject parent, Class<T> type, String id) {
		Optional<Identifier> result = getElementById(parent, id);
		if (!result.isPresent()) {
			return null;
		}
		if (!type.isInstance(result.get())) {
			return null;
		} else {
			return (T) result.get();
		}
	}

	public static Optional<Identifier> getElementById(EObject parent, String id) {
		TreeIterator<EObject> it = parent.eAllContents();
		while (it.hasNext()) {
			EObject eo = it.next();
			if (Identifier.class.isInstance(eo)) {
				Identifier ident = (Identifier) eo;
				if (ident.getId().equals(id)) {
					return Optional.of(ident);
				}
			}
		}
		return Optional.empty();
	}

	private static void initPathmaps() {
		final String metricSpecModel = "models/Palladio.resourcetype";
		final URL url = PCMUtils.class.getClassLoader().getResource(metricSpecModel);
		if (url == null) {
			throw new RuntimeException("Error getting common metric definitions");
		}
		String urlString = url.toString();
		if (!urlString.endsWith(metricSpecModel)) {
			throw new RuntimeException("Error getting common metric definitions. Got: " + urlString);
		}
		urlString = urlString.substring(0, urlString.length() - metricSpecModel.length() - 1);
		final URI uri = URI.createURI(urlString);
		final URI target = uri.appendSegment("models").appendSegment("");
		URIConverter.URI_MAP.put(URI.createURI("pathmap://PCM_MODELS/"), target);

		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		final Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("resourcetype", new XMIResourceFactoryImpl());
	}

}
