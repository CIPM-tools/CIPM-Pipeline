package dmodel.pipeline.shared.config;

import java.util.List;

public class ProjectConfiguration {

	private String rootPath;
	private List<String> sourceFolders;
	private String instrumentedPath;

	private String correspondencePath;

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

	public String getInstrumentedPath() {
		return instrumentedPath;
	}

	public void setInstrumentedPath(String instrumentedPath) {
		this.instrumentedPath = instrumentedPath;
	}

	public String getCorrespondencePath() {
		return correspondencePath;
	}

	public void setCorrespondencePath(String correspondencePath) {
		this.correspondencePath = correspondencePath;
	}

}
