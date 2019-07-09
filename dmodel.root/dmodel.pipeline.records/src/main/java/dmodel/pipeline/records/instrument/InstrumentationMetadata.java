package dmodel.pipeline.records.instrument;

import org.palladiosimulator.pcm.repository.Repository;

import tools.vitruv.framework.correspondence.Correspondences;
import tools.vitruv.models.im.InstrumentationModel;

public class InstrumentationMetadata {

	private Repository repository;
	private Correspondences correspondence;
	private InstrumentationModel probes;

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public Correspondences getCorrespondence() {
		return correspondence;
	}

	public void setCorrespondence(Correspondences correspondence) {
		this.correspondence = correspondence;
	}

	public InstrumentationModel getProbes() {
		return probes;
	}

	public void setProbes(InstrumentationModel probes) {
		this.probes = probes;
	}

}
