package dmodel.pipeline.rt.rest.dt.async;

import java.util.List;

import org.palladiosimulator.pcm.repository.OperationInterface;

import dmodel.pipeline.core.ICallGraphProvider;
import dmodel.pipeline.dt.system.pcm.data.AbstractConflict;
import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder;
import dmodel.pipeline.shared.util.AbstractObservable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StartBuildingSystemProcess extends AbstractObservable<AbstractConflict<?>> implements Runnable {
	private PCMSystemBuilder builder;
	private ICallGraphProvider provider;
	private List<OperationInterface> systemInterfaces;

	@Override
	public void run() {
		boolean finished = builder.startBuildingSystem(provider.provideCallGraph(), systemInterfaces);
		if (finished) {
			this.flood(null);
		} else {
			this.flood(builder.getCurrentConflict());
		}
	}

}
