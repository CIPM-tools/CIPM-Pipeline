package dmodel.pipeline.dt.system.pcm.data;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.repository.RepositoryComponent;

import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder.AssemblyRequiredRole;
import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder.SystemProvidedRole;
import dmodel.pipeline.dt.system.pcm.impl.util.Xor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConnectionConflict extends AbstractConflict<RepositoryComponent> {
	@EqualsAndHashCode.Exclude
	private List<RepositoryComponent> solutions;

	@EqualsAndHashCode.Exclude
	private Xor<AssemblyRequiredRole, SystemProvidedRole> target;

	private long id;

	public ConnectionConflict(long id) {
		super();
		this.solutions = new ArrayList<>();
		this.id = id;
	}
}
