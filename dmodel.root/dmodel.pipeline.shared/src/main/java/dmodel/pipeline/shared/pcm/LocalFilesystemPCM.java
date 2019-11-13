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

}