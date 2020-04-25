package dmodel.base.shared.util;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractObservable<T> {

	private List<IGenericListener<T>> listeners;

	public AbstractObservable() {
		this.listeners = new ArrayList<>();
	}

	public List<IGenericListener<T>> getListeners() {
		return listeners;
	}

	public void addListener(IGenericListener<T> listener) {
		this.listeners.add(listener);
	}

	protected void flood(T data) {
		listeners.forEach(l -> {
			l.inform(data);
		});
	}

}
