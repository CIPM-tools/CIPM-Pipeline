package dmodel.pipeline.rt.rest.dt.data.system;

import java.util.ArrayList;
import java.util.List;

public class JsonSystemAssembly {

	private String name;
	private String componentName;
	private String componentId;
	private String id;

	private List<JsonSystemProvidedRole> provided;
	private List<JsonSystemRequiredRole> required;

	public JsonSystemAssembly() {
		this.provided = new ArrayList<>();
		this.required = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<JsonSystemProvidedRole> getProvided() {
		return provided;
	}

	public void setProvided(List<JsonSystemProvidedRole> provided) {
		this.provided = provided;
	}

	public List<JsonSystemRequiredRole> getRequired() {
		return required;
	}

	public void setRequired(List<JsonSystemRequiredRole> required) {
		this.required = required;
	}

}
