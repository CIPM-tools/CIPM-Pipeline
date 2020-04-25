package dmodel.runtime.pipeline.pcm.repository.model;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

public interface IResourceDemandModel {

	public void put(ResourceDemandTriple triple);

	public List<String> getDependentParameters(float thres);

	public Pair<PCMRandomVariable, Double[]> deriveStochasticExpression(float thres);

}
