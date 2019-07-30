package dmodel.pipeline.rt.entry.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "project")
public class ProjectConfiguration {

	private String rootPath;
	private List<String> sourceFolders;
	private String instrumentedPath;

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

}
