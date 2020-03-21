package dmodel.pipeline.evaluation.dt.system.cocome;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;

import dmodel.pipeline.evaluation.dt.system.DTSystemExtractionCommandLineTool;
import dmodel.pipeline.evaluation.dt.system.DTSystemExtractionConfiguration;

public class CoCoMESystemExtractionDT {

	public static void main(String[] args) {
		File repositoryFile = new File("data/dt-system/cocome/cocome.repository");
		File callGraphFile = new File("data/dt-system/cocome/callgraph.scg");
		File outputFile = new File("data/dt-system/cocome/output.system");
		List<String> systemProvidedInterfaces = Lists.newArrayList("_WoD42tCjEduC9O_qbthgAg");

		DTSystemExtractionConfiguration configuration = DTSystemExtractionConfiguration.builder()
				.repositoryFile(repositoryFile).systemOutputFile(outputFile).scgFile(callGraphFile)
				.systemProvidedInterfaceIds(systemProvidedInterfaces).build();
		DTSystemExtractionCommandLineTool clt = new DTSystemExtractionCommandLineTool();

		clt.startSystemExtraction(configuration);

	}

}
