package dmodel.base.evaluation.dt.system;

import java.io.File;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration for the system extraction tool which uses the console to print
 * and resolve conflicts.
 * 
 * @author David Monschein
 *
 */
@Data
@Builder
public class DTSystemExtractionConfiguration {

	/**
	 * Output file for the final system model.
	 */
	private File systemOutputFile;

	/**
	 * Input file which contains the service call graph that should be used to
	 * automatically resolve conflicts.
	 */
	private File scgFile;

	/**
	 * The repository file of the existing PCM.
	 */
	private File repositoryFile;

	/**
	 * The interface IDs that should be provided by the system.
	 */
	private List<String> systemProvidedInterfaceIds;

}
