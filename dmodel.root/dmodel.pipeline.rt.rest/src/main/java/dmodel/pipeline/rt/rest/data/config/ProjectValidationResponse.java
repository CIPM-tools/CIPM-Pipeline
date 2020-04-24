package dmodel.pipeline.rt.rest.data.config;

import lombok.Data;

@Data
public class ProjectValidationResponse {

	private ProjectType type;
	private boolean valid;
	private String typeAsText;
	private ProjectSourceFolderResponse possibleFolders;

}
