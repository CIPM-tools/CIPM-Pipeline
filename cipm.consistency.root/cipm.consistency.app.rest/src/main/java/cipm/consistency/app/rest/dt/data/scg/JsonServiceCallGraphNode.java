package cipm.consistency.app.rest.dt.data.scg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonServiceCallGraphNode {

	private String id;
	private String name;
	private String parentName;
	private String parentId;

	private String containerName;

}
