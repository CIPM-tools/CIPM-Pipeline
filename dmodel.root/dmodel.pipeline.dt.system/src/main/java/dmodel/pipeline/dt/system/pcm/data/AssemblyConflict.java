package dmodel.pipeline.dt.system.pcm.data;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssemblyConflict extends AbstractConflict<AssemblyContext> {
	@EqualsAndHashCode.Exclude
	private List<AssemblyContext> poss;

	@EqualsAndHashCode.Exclude
	private RequiredRole reqRole;

	private long id;

	@EqualsAndHashCode.Exclude
	private ResourceDemandingSEFF serviceTo;

	public AssemblyConflict(long id) {
		super();
		this.poss = new ArrayList<>();
		this.id = id;
	}

}
