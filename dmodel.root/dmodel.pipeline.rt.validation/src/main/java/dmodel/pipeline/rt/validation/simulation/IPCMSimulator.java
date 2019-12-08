package dmodel.pipeline.rt.validation.simulation;

import org.pcm.headless.api.client.ISimulationResultListener;
import org.pcm.headless.shared.data.results.InMemoryResultRepository;

import dmodel.pipeline.shared.pcm.InMemoryPCM;

public interface IPCMSimulator {

	public InMemoryResultRepository simulateBlocking(InMemoryPCM pcm, String name);

	public void simulate(InMemoryPCM pcm, String name, ISimulationResultListener listener);

}
