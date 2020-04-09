package dmodel.pipeline.instrumentation.project;

import java.io.File;
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
		if (!rootPath.substring(rootPath.length() - 1).equals(File.separator)) {
			rootPath += File.separator;
		}
		this.rootPath = rootPath;
	}

	public List<String> getSourceFolders() {
		return sourceFolders;
	}

	public void setSourceFolders(List<String> sourceFolders) {
		this.sourceFolders = sourceFolders;
	}

}
