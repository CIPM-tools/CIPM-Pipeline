package dmodel.base.core.facade.pcm.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.pcm.headless.api.util.PCMUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.uka.ipd.sdq.identifier.Identifier;
import dmodel.base.core.IPcmModelProvider;
import dmodel.base.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.base.shared.pcm.util.repository.PCMRepositoryUtil;

/**
 * Implementation of the repository facade for accessing the underlying
 * repository model. Uses caching between ID string and elements to speedup
 * accesses.
 * 
 * @author David Monschein
 *
 */
@Component
public class RepositoryQueryFacadeImpl implements IRepositoryQueryFacade {
	/**
	 * Provider of the underlying models.
	 */
	@Autowired
	private IPcmModelProvider pcmModelProvider;

	/**
	 * Cache that maps ID string to the corresponding elements in the repository
	 * model.
	 */
	private Cache<String, Identifier> elementIdCache = new Cache2kBuilder<String, Identifier>() {
	}.eternal(true).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset(boolean hard) {
		elementIdCache.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Identifier> T getElementById(String id, Class<T> type) {
		if (elementIdCache.containsKey(id)) {
			return type.cast(elementIdCache.get(id));
		} else {
			T element = PCMUtil.getElementById(pcmModelProvider.getRepository(), type, id);
			if (element != null) {
				elementIdCache.put(id, element);
			}
			return element;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RepositoryComponent> getComponentsProvidingInterface(OperationInterface iface) {
		return PCMRepositoryUtil.getComponentsProvidingInterface(pcmModelProvider.getRepository(), iface);
	}

}
