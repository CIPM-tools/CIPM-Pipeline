package dmodel.designtime.system.pcm.data;

import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

import dmodel.designtime.system.pcm.impl.PCMSystemBuilder;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.AssemblyRequiredRole;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.SystemProvidedRole;
import dmodel.designtime.system.pcm.impl.util.Xor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Conflict type that is used by the {@link PCMSystemBuilder} to indicate that
 * there are multiple assembly contexts that can be used to satisfy a required
 * role.
 * 
 * @author David Monschein
 *
 */
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

	/**
	 * Creates a new unsolved conflict instance with a given ID.
	 * 
	 * @param id the ID for the conflict
	 */
	public AssemblyConflict(long id) {
		super();
		this.id = id;
	}

}
