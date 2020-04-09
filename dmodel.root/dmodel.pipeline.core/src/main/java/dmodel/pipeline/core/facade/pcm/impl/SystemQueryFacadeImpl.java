package dmodel.pipeline.core.facade.pcm.impl;

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

import dmodel.pipeline.core.facade.pcm.ISystemQueryFacade;

public class SystemQueryFacadeImpl implements ISystemQueryFacade {

	@Override
	public void reset(boolean hard) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getAssemblyIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<AssemblyConnector> getAssemblyConnectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<AssemblyContext> getAssemblyContexts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ProvidedDelegationConnector> getProvidedDelegationConnectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<RequiredDelegationConnector> getRequiredDelegationConnectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeConnectors(List<Connector> connectors) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<OperationInterface> getProvidedInterfaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationProvidedRole getProvidedRoleBySignature(Signature describedService__SEFF) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssemblyContext getAssemblyById(String assembly) {
		// TODO Auto-generated method stub
		return null;
	}

}
