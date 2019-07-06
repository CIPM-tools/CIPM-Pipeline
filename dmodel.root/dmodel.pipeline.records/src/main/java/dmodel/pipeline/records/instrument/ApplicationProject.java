package dmodel.pipeline.records.instrument;

import java.util.ArrayList;
import java.util.List;

public class ApplicationProject {

	private String rootPath;
	private List<String> sourceFolders;

	public ApplicationProject() {
		this.sourceFolders = new ArrayList<>();
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public List<String> getSourceFolders() {
		return sourceFolders;
	}

	public void setSourceFolders(List<String> sourceFolders) {
		this.sourceFolders = sourceFolders;
	}

}
