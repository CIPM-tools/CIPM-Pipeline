package dmodel.pipeline.rt.rest.dt.data.system;

import java.util.ArrayList;
import java.util.List;

public class JsonSystemComposite {
	private String name;
	private String id;

	private List<JsonSystemAssembly> assemblys;
	private List<JsonSystemConnector> connectors;
	private List<JsonSystemProvidedRole> provided;
	private List<JsonSystemRequiredRole> required;

	public JsonSystemComposite() {
		this.setAssemblys(new ArrayList<>());
		this.setConnectors(new ArrayList<>());
		this.setRequired(new ArrayList<>());
		this.setProvided(new ArrayList<>());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<JsonSystemAssembly> getAssemblys() {
		return assemblys;
	}

	public void setAssemblys(List<JsonSystemAssembly> assemblys) {
		this.assemblys = assemblys;
	}

	public List<JsonSystemConnector> getConnectors() {
		return connectors;
	}

	public void setConnectors(List<JsonSystemConnector> connectors) {
		this.connectors = connectors;
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
