package dmodel.pipeline.core.facade.pcm;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;

import dmodel.pipeline.core.facade.IResettableQueryFacade;

public interface ISystemQueryFacade extends IResettableQueryFacade {

	Set<String> getAssemblyIds();

	Collection<AssemblyConnector> getAssemblyConnectors();

	Collection<AssemblyContext> getAssemblyContexts();

	Collection<ProvidedDelegationConnector> getProvidedDelegationConnectors();

	Collection<RequiredDelegationConnector> getRequiredDelegationConnectors();

	String getId();

	void removeConnectors(List<Connector> connectors);

	List<OperationInterface> getProvidedInterfaces();

	OperationProvidedRole getProvidedRoleBySignature(Signature describedService__SEFF);

	/**
	 * system.getProvidedRoles_InterfaceProvidingEntity().stream().filter(pr -> { if
	 * (pr instanceof OperationProvidedRole) { return ((OperationProvidedRole)
	 * pr).getProvidedInterface__OperationProvidedRole()
	 * .getSignatures__OperationInterface().stream().anyMatch(op -> { return
	 * op.getId().equals(seff.getDescribedService__SEFF().getId()); }); } return
	 * false; }).map(pr -> (OperationProvidedRole) pr).findFirst().orElse(null)
	 */

	AssemblyContext getAssemblyById(String assembly);

}
