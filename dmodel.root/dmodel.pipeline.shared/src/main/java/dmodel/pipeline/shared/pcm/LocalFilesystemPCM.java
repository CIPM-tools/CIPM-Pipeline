package dmodel.pipeline.shared.pcm;

import java.io.File;

import lombok.Data;

@Data
public class LocalFilesystemPCM {

	private File repositoryFile;
	private File systemFile;
	private File usageModelFile;
	private File allocationModelFile;
	private File resourceEnvironmentFile;

	public boolean isValid() {
		return repositoryFile != null && systemFile != null && usageModelFile != null && allocationModelFile != null
				&& resourceEnvironmentFile != null && repositoryFile.exists() && systemFile.exists()
				&& usageModelFile.exists() && allocationModelFile.exists() && resourceEnvironmentFile.exists();
	}

}