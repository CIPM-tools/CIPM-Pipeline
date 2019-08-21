package dmodel.pipeline.shared.pcm;

import java.io.File;

public class LocalFilesystemPCM {
	private File repositoryFile;
	private File systemFile;
	private File usageModelFile;
	private File allocationModelFile;
	private File resourceEnvironmentFile;

	public File getRepositoryFile() {
		return repositoryFile;
	}

	public void setRepositoryFile(File repositoryFile) {
		this.repositoryFile = repositoryFile;
	}

	public File getSystemFile() {
		return systemFile;
	}

	public void setSystemFile(File systemFile) {
		this.systemFile = systemFile;
	}

	public File getUsageModelFile() {
		return usageModelFile;
	}

	public void setUsageModelFile(File usageModelFile) {
		this.usageModelFile = usageModelFile;
	}

	public File getAllocationModelFile() {
		return allocationModelFile;
	}

	public void setAllocationModelFile(File allocationModelFile) {
		this.allocationModelFile = allocationModelFile;
	}

	public File getResourceEnvironmentFile() {
		return resourceEnvironmentFile;
	}

	public void setResourceEnvironmentFile(File resourceEnvironmentFile) {
		this.resourceEnvironmentFile = resourceEnvironmentFile;
	}

}