package cipm.consistency.runtime.pipeline.pcm.repository;

import org.palladiosimulator.pcm.core.PCMRandomVariable;

public interface IRegressionModelNoiseGenerator {

	public PCMRandomVariable generateNoise(double[] deviations, int maxSize);

}
