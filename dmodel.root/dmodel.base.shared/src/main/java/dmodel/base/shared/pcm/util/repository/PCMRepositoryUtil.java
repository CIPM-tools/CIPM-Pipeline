package dmodel.base.shared.pcm.util.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

public class PCMRepositoryUtil {

	public static List<RepositoryComponent> getComponentsProvidingInterface(Repository baseRepository,
			OperationInterface iface) {
		return baseRepository.getComponents__Repository().stream().filter(comp -> {
			return comp.getProvidedRoles_InterfaceProvidingEntity().stream().anyMatch(pr -> {
				if (pr instanceof OperationProvidedRole) {
					return ((OperationProvidedRole) pr).getProvidedInterface__OperationProvidedRole().getId()
							.equals(iface.getId());
				}
				return false;
			});
		}).collect(Collectors.toList());
	}

	public static OperationRequiredRole getCorrespondingRequiredRole(ResourceDemandingSEFF fromSeff,
			ResourceDemandingSEFF toSeff) {
		return fromSeff.getBasicComponent_ServiceEffectSpecification().getRequiredRoles_InterfaceRequiringEntity()
				.stream().filter(req -> {
					if (req instanceof OperationRequiredRole) {
						return ((OperationRequiredRole) req).getRequiredInterface__OperationRequiredRole()
								.getSignatures__OperationInterface().stream()
								.anyMatch(sig -> sig.getId().equals(toSeff.getDescribedService__SEFF().getId()));
					}
					return false;
				}).map(r -> (OperationRequiredRole) r).findFirst().orElse(null);
	}

	public static ResourceDemandingSEFF getParentService(AbstractAction ext) {
		EObject current = ext;
		while (current != null && !(current instanceof ResourceDemandingSEFF)) {
			current = current.eContainer();
		}

		if (current != null && current instanceof ResourceDemandingSEFF) {
			return (ResourceDemandingSEFF) current;
		} else {
			return null;
		}
	}

}
