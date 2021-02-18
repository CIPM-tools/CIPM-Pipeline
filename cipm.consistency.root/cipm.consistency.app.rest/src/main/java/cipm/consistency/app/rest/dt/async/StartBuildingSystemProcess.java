package cipm.consistency.app.rest.dt.async;

import java.util.List;

import org.palladiosimulator.pcm.repository.OperationInterface;

import cipm.consistency.base.core.ICallGraphProvider;
import cipm.consistency.base.shared.util.AbstractObservable;
import cipm.consistency.designtime.systemextraction.pcm.data.AbstractConflict;
import cipm.consistency.designtime.systemextraction.pcm.impl.PCMSystemBuilder;
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
