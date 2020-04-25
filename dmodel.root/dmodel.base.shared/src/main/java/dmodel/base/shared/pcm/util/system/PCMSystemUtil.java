package dmodel.base.shared.pcm.util.system;

import java.util.Optional;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ComposedStructure;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.system.System;

public class PCMSystemUtil {

	public static AssemblyConnector createAssemblyConnector(ComposedStructure currentOuterStructure,
			OperationProvidedRole prov, AssemblyContext providing, OperationRequiredRole req,
			AssemblyContext requiring) {
		AssemblyConnector nConnector = CompositionFactory.eINSTANCE.createAssemblyConnector();
		nConnector.setProvidedRole_AssemblyConnector(prov);
		nConnector.setRequiredRole_AssemblyConnector(req);
		nConnector.setProvidingAssemblyContext_AssemblyConnector(providing);
		nConnector.setRequiringAssemblyContext_AssemblyConnector(requiring);

		nConnector.setEntityName("Connector " + requiring.getEntityName() + " -> " + providing.getEntityName());

		currentOuterStructure.getConnectors__ComposedStructure().add(nConnector);

		return nConnector;
	}

	public static Optional<RequiredRole> getRequiredRoleBySignature(RepositoryComponent comp, Signature toSig) {
		return comp.getRequiredRoles_InterfaceRequiringEntity().stream().filter(r -> {
			if (r instanceof OperationRequiredRole) {
				return ((OperationRequiredRole) r).getRequiredInterface__OperationRequiredRole()
						.getSignatures__OperationInterface().parallelStream()
						.anyMatch(sig -> sig.getId().equals(toSig.getId()));
			}
			return false;
		}).findFirst();
	}

	public static Optional<ProvidedRole> getProvidedRoleBySignature(RepositoryComponent comp, Signature toSig) {
		return comp.getProvidedRoles_InterfaceProvidingEntity().stream().filter(r -> {
			if (r instanceof OperationProvidedRole) {
				return ((OperationProvidedRole) r).getProvidedInterface__OperationProvidedRole()
						.getSignatures__OperationInterface().parallelStream()
						.anyMatch(sig -> sig.getId().equals(toSig.getId()));
			}
			return false;
		}).findFirst();
	}

	public static ProvidedDelegationConnector createProvidedDelegation(System system,
			OperationProvidedRole outerProvidedRole, AssemblyContext ctx, OperationProvidedRole innerProvidedRole) {
		return createProvidedDelegation(system, outerProvidedRole, ctx, innerProvidedRole, true);
	}

	public static ProvidedDelegationConnector createProvidedDelegation(System system,
			OperationProvidedRole outerProvidedRole, AssemblyContext ctx, OperationProvidedRole innerProvidedRole,
			boolean add) {
		ProvidedDelegationConnector conn = CompositionFactory.eINSTANCE.createProvidedDelegationConnector();

		conn.setAssemblyContext_ProvidedDelegationConnector(ctx);
		conn.setInnerProvidedRole_ProvidedDelegationConnector(innerProvidedRole);
		conn.setOuterProvidedRole_ProvidedDelegationConnector(outerProvidedRole);
		conn.setEntityName(
				"ProvDelegation " + outerProvidedRole.getEntityName() + " -> " + innerProvidedRole.getEntityName());

		if (add) {
			conn.setParentStructure__Connector(system);
			system.getConnectors__ComposedStructure().add(conn);
		}

		return conn;
	}

	public static AssemblyContext createAssemblyContext(System system, RepositoryComponent comp) {
		AssemblyContext ctx = CompositionFactory.eINSTANCE.createAssemblyContext();
		ctx.setEncapsulatedComponent__AssemblyContext(comp);
		system.getAssemblyContexts__ComposedStructure().add(ctx);
		return ctx;
	}

	public static AssemblyContext createAssemblyContext(System system, RepositoryComponent comp, String name) {
		AssemblyContext ctx = CompositionFactory.eINSTANCE.createAssemblyContext();
		ctx.setEncapsulatedComponent__AssemblyContext(comp);
		system.getAssemblyContexts__ComposedStructure().add(ctx);
		ctx.setEntityName(name);
		return ctx;
	}

	public static OperationProvidedRole createProvidedRole(System currentSystem, OperationInterface entryPoint) {
		OperationProvidedRole output = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		output.setProvidedInterface__OperationProvidedRole(entryPoint);
		output.setEntityName("SystemProvided_" + entryPoint.getEntityName());
		currentSystem.getProvidedRoles_InterfaceProvidingEntity().add(output);

		return output;
	}

	public static OperationRequiredRole createRequiredRole(System currentSystem, OperationInterface reqInterface) {
		OperationRequiredRole output = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
		output.setRequiredInterface__OperationRequiredRole(reqInterface);
		currentSystem.getRequiredRoles_InterfaceRequiringEntity().add(output);

		return output;
	}

	public static RequiredDelegationConnector createRequiredDelegation(AssemblyContext ctx, OperationRequiredRole irr,
			System system, OperationRequiredRole role) {
		RequiredDelegationConnector conn = CompositionFactory.eINSTANCE.createRequiredDelegationConnector();

		conn.setAssemblyContext_RequiredDelegationConnector(ctx);
		conn.setInnerRequiredRole_RequiredDelegationConnector(role);
		conn.setOuterRequiredRole_RequiredDelegationConnector(role);
		conn.setEntityName("ProvDelegation " + irr.getEntityName() + " -> " + role.getEntityName());
		conn.setParentStructure__Connector(system);
		system.getConnectors__ComposedStructure().add(conn);

		return conn;
	}

}
