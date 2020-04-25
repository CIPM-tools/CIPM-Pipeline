package dmodel.app.rest.dt.async;

import java.util.List;

import org.palladiosimulator.pcm.repository.OperationInterface;

import dmodel.base.core.ICallGraphProvider;
import dmodel.base.shared.util.AbstractObservable;
import dmodel.designtime.system.pcm.data.AbstractConflict;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder;
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
