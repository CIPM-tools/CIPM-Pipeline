package dmodel.pipeline.shared.util;

public class SimpleObjectContainer<T> {

	private T data = null;
	private boolean set = false;

	public void setData(T data) {
		this.data = data;
		this.set = true;
	}

	public boolean isSet() {
		return set;
	}

	public T getData() {
		return data;
	}

}
