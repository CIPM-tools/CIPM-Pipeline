package dmodel.app.rest.dt.data.system;

import lombok.Data;

@Data
public class JsonBuildingConflict {

	private long id;
	private String type;

	private String[] possibleIds;
	private String[] targetIds;

}
