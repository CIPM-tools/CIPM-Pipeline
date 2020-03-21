package dmodel.pipeline.rt.rest.dt.async;

import java.util.List;

import org.palladiosimulator.pcm.repository.OperationInterface;

import dmodel.pipeline.dt.system.pcm.data.AbstractConflict;
import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder;
import dmodel.pipeline.rt.pipeline.border.RunTimeDesignTimeBorder;
import dmodel.pipeline.shared.util.AbstractObservable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StartBuildingSystemProcess extends AbstractObservable<AbstractConflict<?>> implements Runnable {
	private PCMSystemBuilder builder;
	private RunTimeDesignTimeBorder border;
	private List<OperationInterface> systemInterfaces;

	@Override
	public void run() {
		boolean finished = builder.startBuildingSystem(border.getServiceCallGraph(), systemInterfaces);
		if (finished) {
			this.flood(null);
		} else {
			this.flood(builder.getCurrentConflict());
		}
	}

}
