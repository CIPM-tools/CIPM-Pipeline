package dmodel.pipeline.core.facade.pcm;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingEntity;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.Signature;

import dmodel.pipeline.core.facade.IResettableQueryFacade;
import dmodel.pipeline.shared.pcm.util.deprecation.IDeprecationProcessor;

public interface ISystemQueryFacade extends IResettableQueryFacade {
	String getId();

	Set<String> getAssemblyIds();

	Collection<AssemblyConnector> getAssemblyConnectors();

	Collection<AssemblyContext> getAssemblyContexts();

	Collection<ProvidedDelegationConnector> getProvidedDelegationConnectors();

	Collection<RequiredDelegationConnector> getRequiredDelegationConnectors();

	List<OperationInterface> getProvidedInterfaces();

	List<OperationProvidedRole> getProvidedRoles();

	List<OperationProvidedRole> getProvidedRoleBySignature(Signature describedService__SEFF);

	List<OperationProvidedRole> getProvidedRoleBySignature(Signature describedService__SEFF,
			InterfaceProvidingEntity entity);

	AssemblyContext getAssemblyById(String assembly);

	AssemblyContext createAssemblyContext(RepositoryComponent component);

	void removeConnectors(List<Connector> connectors);

	boolean hasConnector(AssemblyContext correspondingACtx, OperationRequiredRole requiredRole,
			AssemblyContext correspondingACtxTarget, OperationProvidedRole correspondingProvidedRole);

	boolean hasConnector(OperationProvidedRole outerProvidedRole, AssemblyContext ctx,
			OperationProvidedRole innerProvidedRole);

	boolean hasConnector(OperationRequiredRole outerRequiredRole, AssemblyContext ctx,
			OperationRequiredRole innerRequiredRole);

	AssemblyConnector createConnector(AssemblyContext correspondingACtx, OperationRequiredRole requiredRole,
			AssemblyContext correspondingACtxTarget, OperationProvidedRole correspondingProvidedRole);

	void reconnectOuterProvidedRole(OperationProvidedRole systemProvidedRole, AssemblyContext ctx,
			OperationProvidedRole role);

	void reconnectOuterRequiredRole(OperationRequiredRole selectedOuter, AssemblyContext left,
			OperationRequiredRole right);

	List<Pair<AssemblyContext, OperationRequiredRole>> getUnsatisfiedInnerRequiredRoles();

	List<OperationProvidedRole> getUnsatisfiedOuterProvidedRoles();

	List<OperationRequiredRole> getNonLinkedOuterRequiredRoles();

	// more logic
	void removeInconsistentConnectors(AssemblyConnector connector);

	void processUnreachableAssemblys(IDeprecationProcessor deprecationProcessor);

}
