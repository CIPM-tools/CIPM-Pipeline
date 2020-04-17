package dmodel.pipeline.rt.rest.dt.async;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.system.scg.ServiceCallGraphBuilder;
import dmodel.pipeline.shared.util.AbstractObservable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BuildServiceCallGraphProcess extends AbstractObservable<ServiceCallGraph> implements Runnable {
	private ServiceCallGraphBuilder builder;
	private List<String> jarFiles;
	private String basePath;

	@Override
	public void run() {
		File basePathFile = new File(basePath);
		// extract
		ServiceCallGraph result = builder.buildServiceCallGraph(
				jarFiles.stream().map(jf -> new File(basePathFile, jf)).collect(Collectors.toList()));

		super.flood(result);
	}

}
