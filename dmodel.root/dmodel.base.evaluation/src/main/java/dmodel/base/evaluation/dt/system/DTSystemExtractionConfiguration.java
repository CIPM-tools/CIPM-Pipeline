package dmodel.base.evaluation.dt.system;

import java.io.File;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DTSystemExtractionConfiguration {

	private File systemOutputFile;
	private File scgFile;

	private File repositoryFile;
	private List<String> systemProvidedInterfaceIds;

}
