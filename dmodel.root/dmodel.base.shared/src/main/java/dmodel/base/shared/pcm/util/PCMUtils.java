package dmodel.base.shared.pcm.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.pcm.PcmPackage;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.resourcetype.ResourceRepository;
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

	public static ResourceRepository getDefaultResourceRepository() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

		URI filePathUri = org.eclipse.emf.common.util.URI.createURI("pathmap://PCM_MODELS/Palladio.resourcetype");

		Resource resource = resourceSet.getResource(filePathUri, true);

		return (ResourceRepository) resource.getContents().get(0);
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

	@SuppressWarnings("unchecked")
	public static <T> List<T> getElementsByType(EObject parent, Class<T> class1) {
		List<T> ret = new ArrayList<>();
		TreeIterator<EObject> it = parent.eAllContents();
		while (it.hasNext()) {
			EObject eo = it.next();
			if (class1.isInstance(eo)) {
				ret.add((T) eo);
			}
		}
		return ret;
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

	public static PCMRandomVariable createRandomVariableFromString(String string) {
		PCMRandomVariable var = CoreFactory.eINSTANCE.createPCMRandomVariable();
		var.setSpecification(string);
		return var;
	}

	public static ProvidedRole getProvideRoleByRequiredRole(RepositoryComponent comp, OperationRequiredRole reqRole) {
		return comp.getProvidedRoles_InterfaceProvidingEntity().stream().filter(pr -> {
			if (pr instanceof OperationProvidedRole) {
				OperationProvidedRole opr = (OperationProvidedRole) pr;
				return opr.getProvidedInterface__OperationProvidedRole().getId()
						.equals(reqRole.getRequiredInterface__OperationRequiredRole().getId());
			}
			return false;
		}).findFirst().orElse(null);
	}

}
