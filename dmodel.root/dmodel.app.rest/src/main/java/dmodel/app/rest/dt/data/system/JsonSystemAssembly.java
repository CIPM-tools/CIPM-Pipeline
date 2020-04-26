package dmodel.app.rest.dt.data.system;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
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

}
