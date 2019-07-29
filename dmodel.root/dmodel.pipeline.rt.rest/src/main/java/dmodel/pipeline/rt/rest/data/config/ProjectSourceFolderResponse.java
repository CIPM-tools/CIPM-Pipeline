package dmodel.pipeline.rt.rest.data.config;

import java.util.ArrayList;
import java.util.List;

public class ProjectSourceFolderResponse {
	private List<ProjectSourceFolderResponse> subfolders;
	private String name;

	public ProjectSourceFolderResponse() {
		this.subfolders = new ArrayList<>();
	}

	public List<ProjectSourceFolderResponse> getSubfolders() {
		return subfolders;
	}

	public void setSubfolders(List<ProjectSourceFolderResponse> subfolders) {
		this.subfolders = subfolders;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
