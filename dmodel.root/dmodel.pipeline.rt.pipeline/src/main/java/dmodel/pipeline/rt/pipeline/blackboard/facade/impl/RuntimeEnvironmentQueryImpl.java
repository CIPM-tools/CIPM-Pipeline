package dmodel.pipeline.rt.pipeline.blackboard.facade.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dmodel.pipeline.core.ISpecificModelProvider;
import dmodel.pipeline.core.facade.IRuntimeEnvironmentQueryFacade;
import dmodel.pipeline.rt.runtimeenvironment.REModel.REModelFactory;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainerConnection;
import dmodel.pipeline.vsum.facade.CentralVsumFacade;

@Component
public class RuntimeEnvironmentQueryImpl implements IRuntimeEnvironmentQueryFacade {
	@Autowired
	private CentralVsumFacade vsum;

	@Autowired
	private ISpecificModelProvider remProvider;

	private Cache<String, RuntimeResourceContainer> containerCache = new Cache2kBuilder<String, RuntimeResourceContainer>() {
	}.eternal(true).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();
	private Cache<Pair<String, String>, RuntimeResourceContainerConnection> linkCache = new Cache2kBuilder<Pair<String, String>, RuntimeResourceContainerConnection>() {
	}.eternal(true).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	@Override
	public RuntimeResourceContainer getContainerById(String hostId) {
		// the cache should always have this (except unknown side effects)
		return containerCache.get(hostId);
	}

	@Override
	public RuntimeResourceContainerConnection getLinkByIds(String fromId, String toId) {
		return linkCache.get(Pair.of(fromId, toId));
	}

	@Override
	public boolean containsHostId(String hostId) {
		return containerCache.containsKey(hostId);
	}

	@Override
	public void createResourceContainer(String hostId, String hostName) {
		RuntimeResourceContainer nContainer = REModelFactory.eINSTANCE.createRuntimeResourceContainer();
		nContainer.setHostID(hostId);
		nContainer.setHostname(hostName);
		remProvider.getRuntimeEnvironment().getContainers().add(nContainer);
		containerCache.put(hostId, nContainer);

		vsum.createdObject(nContainer);
	}

	@Override
	public boolean containsLink(String fromId, String toId) {
		return linkCache.containsKey(Pair.of(fromId, toId));
	}

	@Override
	public void createResourceContainerLink(String fromId, String toId) {
		RuntimeResourceContainerConnection nConnection = REModelFactory.eINSTANCE
				.createRuntimeResourceContainerConnection();

		RuntimeResourceContainer containerFrom = this.getContainerById(fromId);
		RuntimeResourceContainer containerTo = this.getContainerById(toId);

		nConnection.setContainerFrom(containerFrom);
		nConnection.setContainerTo(containerTo);
		remProvider.getRuntimeEnvironment().getConnections().add(nConnection);

		linkCache.put(Pair.of(fromId, toId), nConnection);
		linkCache.put(Pair.of(toId, fromId), nConnection);

		vsum.createdObject(nConnection);
	}

	@Override
	public void reset(boolean hard) {
		containerCache.clear();
		linkCache.clear();

		// fill cache
		remProvider.getRuntimeEnvironment().getContainers().stream().forEach(c -> containerCache.put(c.getHostID(), c));
		remProvider.getRuntimeEnvironment().getConnections().stream().forEach(l -> {
			Pair<String, String> pairA = Pair.of(l.getContainerFrom().getHostID(), l.getContainerTo().getHostID());
			Pair<String, String> pairB = Pair.of(l.getContainerTo().getHostID(), l.getContainerFrom().getHostID());
			linkCache.put(pairA, l);
			linkCache.put(pairB, l);
		});
	}

}
