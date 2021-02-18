package cipm.consistency.designtime.systemextraction.pcm.data;

import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

import cipm.consistency.designtime.systemextraction.pcm.impl.PCMSystemBuilder;
import cipm.consistency.designtime.systemextraction.pcm.impl.PCMSystemBuilder.AssemblyRequiredRole;
import cipm.consistency.designtime.systemextraction.pcm.impl.PCMSystemBuilder.SystemProvidedRole;
import cipm.consistency.designtime.systemextraction.pcm.impl.util.Xor;
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
