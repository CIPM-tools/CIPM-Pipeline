package dmodel.app.rest.dt.data.system;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
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

}
