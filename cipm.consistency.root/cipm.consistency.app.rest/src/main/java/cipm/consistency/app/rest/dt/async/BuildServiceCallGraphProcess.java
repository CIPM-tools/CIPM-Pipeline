package cipm.consistency.app.rest.dt.async;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph;
import cipm.consistency.base.shared.util.AbstractObservable;
import cipm.consistency.designtime.systemextraction.scg.ServiceCallGraphBuilder;
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
