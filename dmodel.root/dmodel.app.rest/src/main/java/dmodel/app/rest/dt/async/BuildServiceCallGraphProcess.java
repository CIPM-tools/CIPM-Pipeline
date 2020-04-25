package dmodel.app.rest.dt.async;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.base.shared.util.AbstractObservable;
import dmodel.designtime.system.scg.ServiceCallGraphBuilder;
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
