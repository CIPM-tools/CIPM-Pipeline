package dmodel.designtime.system.pcm.data;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.repository.RepositoryComponent;

import dmodel.designtime.system.pcm.impl.PCMSystemBuilder;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.AssemblyRequiredRole;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.SystemProvidedRole;
import dmodel.designtime.system.pcm.impl.util.Xor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Conflict type that is used by the {@link PCMSystemBuilder} to indicate that
 * there are multiple components that can be used to satisfy a required role.
 * 
 * @author David Monschein
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ConnectionConflict extends AbstractConflict<RepositoryComponent> {
	@EqualsAndHashCode.Exclude
	private List<RepositoryComponent> solutions;

	@EqualsAndHashCode.Exclude
	private Xor<AssemblyRequiredRole, SystemProvidedRole> target;

	private long id;

	/**
	 * Creates a new and unsolved conflict instance with a given ID.
	 * 
	 * @param id the ID of the conflict
	 */
	public ConnectionConflict(long id) {
		super();
		this.solutions = new ArrayList<>();
		this.id = id;
	}
}
