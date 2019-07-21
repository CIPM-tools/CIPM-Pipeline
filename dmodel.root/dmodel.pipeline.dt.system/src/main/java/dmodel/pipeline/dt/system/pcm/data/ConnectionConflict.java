package dmodel.pipeline.dt.system.pcm.data;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RequiredRole;

public class ConnectionConflict extends AbstractConflict<ProvidedRole> {
	private List<ProvidedRole> provided;
	private RequiredRole required;

	private long id;

	public ConnectionConflict(long id) {
		super();
		this.provided = new ArrayList<>();
		this.id = id;
	}

	public List<ProvidedRole> getProvided() {
		return provided;
	}

	public void setProvided(List<ProvidedRole> provided) {
		this.provided = provided;
	}

	public RequiredRole getRequired() {
		return required;
	}

	public void setRequired(RequiredRole required) {
		this.required = required;
	}

	public long getId() {
		return id;
	}
}
