package dmodel.pipeline.dt.system.pcm.data;

import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder.AssemblyRequiredRole;
import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder.SystemProvidedRole;
import dmodel.pipeline.dt.system.pcm.impl.util.Xor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssemblyConflict extends AbstractConflict<AssemblyContext> {
	@EqualsAndHashCode.Exclude
	private List<AssemblyContext> solutions;

	@EqualsAndHashCode.Exclude
	private Xor<AssemblyRequiredRole, SystemProvidedRole> target;

	@EqualsAndHashCode.Exclude
	private RepositoryComponent correspondingComponent;

	private long id;

	public AssemblyConflict(long id) {
		super();
		this.id = id;
	}

}
