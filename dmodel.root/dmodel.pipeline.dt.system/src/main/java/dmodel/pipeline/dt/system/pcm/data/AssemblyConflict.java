package dmodel.pipeline.dt.system.pcm.data;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.RequiredRole;

public class AssemblyConflict extends AbstractConflict<AssemblyContext> {

	private List<AssemblyContext> poss;
	private RequiredRole reqRole;
	private long id;

	public AssemblyConflict(long id) {
		super();
		this.poss = new ArrayList<>();
		this.id = id;
	}

	public List<AssemblyContext> getPoss() {
		return poss;
	}

	public void setPoss(List<AssemblyContext> poss) {
		this.poss = poss;
	}

	public RequiredRole getReqRole() {
		return reqRole;
	}

	public void setReqRole(RequiredRole reqRole) {
		this.reqRole = reqRole;
	}

	public long getId() {
		return id;
	}

}
