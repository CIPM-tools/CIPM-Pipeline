package dmodel.pipeline.records.instrument;

import org.palladiosimulator.pcm.repository.Repository;

import InstrumentationMetamodel.InstrumentationModel;

public class InstrumentationMetadata {
	private Repository repository;
	private InstrumentationModel probes;

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public InstrumentationModel getProbes() {
		return probes;
	}

	public void setProbes(InstrumentationModel probes) {
		this.probes = probes;
	}

}
