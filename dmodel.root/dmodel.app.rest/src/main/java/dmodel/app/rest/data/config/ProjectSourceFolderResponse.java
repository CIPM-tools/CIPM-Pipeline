package dmodel.app.rest.data.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ProjectSourceFolderResponse {
	private List<ProjectSourceFolderResponse> subfolders;
	private String name;

	public ProjectSourceFolderResponse() {
		this.subfolders = new ArrayList<>();
	}

}
