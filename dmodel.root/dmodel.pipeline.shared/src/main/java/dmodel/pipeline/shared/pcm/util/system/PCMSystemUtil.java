package dmodel.pipeline.shared.pcm.util.system;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.system.System;

public class PCMSystemUtil {

	public static AssemblyConnector createAssemblyConnector(System system, OperationProvidedRole prov,
			AssemblyContext providing, OperationRequiredRole req, AssemblyContext requiring) {
		AssemblyConnector nConnector = CompositionFactory.eINSTANCE.createAssemblyConnector();
		nConnector.setProvidedRole_AssemblyConnector(prov);
		nConnector.setRequiredRole_AssemblyConnector(req);
		nConnector.setProvidingAssemblyContext_AssemblyConnector(providing);
		nConnector.setRequiringAssemblyContext_AssemblyConnector(requiring);

		nConnector.setEntityName("Connector " + requiring.getEntityName() + " -> " + providing.getEntityName());

		system.getConnectors__ComposedStructure().add(nConnector);

		return nConnector;
	}

	public static ProvidedDelegationConnector createProvidedDelegation(System system, OperationProvidedRole role,
			AssemblyContext ctx, OperationProvidedRole spr) {
		return createProvidedDelegation(system, role, ctx, spr, true);
	}

	public static ProvidedDelegationConnector createProvidedDelegation(System system, OperationProvidedRole role,
			AssemblyContext ctx, OperationProvidedRole spr, boolean add) {
		ProvidedDelegationConnector conn = CompositionFactory.eINSTANCE.createProvidedDelegationConnector();

		conn.setAssemblyContext_ProvidedDelegationConnector(ctx);
		conn.setInnerProvidedRole_ProvidedDelegationConnector(role);
		conn.setOuterProvidedRole_ProvidedDelegationConnector(spr);

		if (add) {
			conn.setParentStructure__Connector(system);
			system.getConnectors__ComposedStructure().add(conn);
		}

		return conn;
	}

	public static AssemblyContext createAssemblyContext(System system, RepositoryComponent comp) {
		AssemblyContext ctx = CompositionFactory.eINSTANCE.createAssemblyContext();
		ctx.setEncapsulatedComponent__AssemblyContext(comp);
		return ctx;
	}

}
