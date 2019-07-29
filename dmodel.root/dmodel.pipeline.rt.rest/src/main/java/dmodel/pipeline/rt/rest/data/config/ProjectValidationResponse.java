package dmodel.pipeline.rt.rest.data.config;

public class ProjectValidationResponse {

	private ProjectType type;
	private boolean valid;
	private String typeAsText;
	private ProjectSourceFolderResponse possibleFolders;

	public ProjectType getType() {
		return type;
	}

	public void setType(ProjectType type) {
		this.type = type;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getTypeAsText() {
		return typeAsText;
	}

	public void setTypeAsText(String typeAsText) {
		this.typeAsText = typeAsText;
	}

	public ProjectSourceFolderResponse getPossibleFolders() {
		return possibleFolders;
	}

	public void setPossibleFolders(ProjectSourceFolderResponse possibleFolders) {
		this.possibleFolders = possibleFolders;
	}

}
