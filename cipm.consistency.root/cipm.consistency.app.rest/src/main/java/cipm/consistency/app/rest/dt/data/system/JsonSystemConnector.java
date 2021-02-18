package cipm.consistency.app.rest.dt.data.system;

import lombok.Data;

@Data
public class JsonSystemConnector {

	private boolean delegation;
	private boolean delegationDirection;

	private String assemblyFrom;
	private String assemblyTo;

	private String role1;
	private String role2;

	private String id;
	private String name;

}
