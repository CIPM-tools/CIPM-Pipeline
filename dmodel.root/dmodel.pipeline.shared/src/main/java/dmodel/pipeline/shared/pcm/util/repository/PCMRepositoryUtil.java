package dmodel.pipeline.shared.pcm.util.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

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

}
