package dmodel.pipeline.evaluation.dt.system.cocome;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import dmodel.pipeline.evaluation.dt.system.DTSystemExtractionCommandLineTool;
import dmodel.pipeline.evaluation.dt.system.DTSystemExtractionConfiguration;

@SpringBootApplication
@ComponentScans({ @ComponentScan("dmodel.pipeline.dt.system"), @ComponentScan("dmodel.pipeline.evaluation") })
@Component
public class CoCoMESystemExtractionDT {

	@Autowired
	private DTSystemExtractionCommandLineTool clt;

	public static void main(String[] args) {
		new SpringApplicationBuilder(CoCoMESystemExtractionDT.class).web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
				.run(args);
	}

	@PostConstruct
	public void extract() {
		File repositoryFile = new File("data/dt-system/cocome/cocome.repository");
		File callGraphFile = new File("data/dt-system/cocome/callgraph.scg");
		File outputFile = new File("data/dt-system/cocome/output.system");
		List<String> systemProvidedInterfaces = Lists.newArrayList("_WoD42tCjEduC9O_qbthgAg");

		DTSystemExtractionConfiguration configuration = DTSystemExtractionConfiguration.builder()
				.repositoryFile(repositoryFile).systemOutputFile(outputFile).scgFile(callGraphFile)
				.systemProvidedInterfaceIds(systemProvidedInterfaces).build();

		clt.startSystemExtraction(configuration);
	}

}
