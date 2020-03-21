package dmodel.pipeline.rt.rest.dt.data;

import java.util.List;

public class JsonRepository {

	private List<JsonRepositoryComponent> components;
	private List<JsonRepositoryInterface> interfaces;

	public List<JsonRepositoryComponent> getComponents() {
		return components;
	}

	public void setComponents(List<JsonRepositoryComponent> components) {
		this.components = components;
	}

	public List<JsonRepositoryInterface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<JsonRepositoryInterface> interfaces) {
		this.interfaces = interfaces;
	}

}
