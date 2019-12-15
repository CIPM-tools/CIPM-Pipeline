package dmodel.pipeline.shared.pcm.util;

import java.util.concurrent.TimeUnit;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.eclipse.emf.ecore.EObject;

import de.uka.ipd.sdq.identifier.Identifier;

public class PCMElementIDCache<T extends Identifier> {

	private Cache<String, T> cache;

	private Class<T> clazz;

	public PCMElementIDCache(Class<T> clazz) {
		this.clazz = clazz;
		this.cache = Cache2kBuilder.of(String.class, clazz).expireAfterWrite(5, TimeUnit.MINUTES)
				.resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();
	}

	public void clear() {
		cache.clear();
	}

	public T resolve(EObject parent, String id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		} else {
			T el = PCMUtils.getElementById(parent, clazz, id);
			if (el != null) {
				cache.put(id, el);
			}
			return el;
		}
	}

}
