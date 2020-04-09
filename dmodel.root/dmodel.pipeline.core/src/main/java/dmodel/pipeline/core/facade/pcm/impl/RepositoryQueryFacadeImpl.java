package dmodel.pipeline.core.facade.pcm.impl;

import java.util.concurrent.TimeUnit;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.pcm.headless.api.util.PCMUtil;
import org.springframework.beans.factory.annotation.Autowired;

import de.uka.ipd.sdq.identifier.Identifier;
import dmodel.pipeline.core.IPcmModelProvider;
import dmodel.pipeline.core.facade.pcm.IRepositoryQueryFacade;

public class RepositoryQueryFacadeImpl implements IRepositoryQueryFacade {
	@Autowired
	private IPcmModelProvider pcmModelProvider;

	private Cache<String, Identifier> elementIdCache = new Cache2kBuilder<String, Identifier>() {
	}.eternal(true).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	@Override
	public void reset(boolean hard) {
		elementIdCache.clear();
	}

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

}
