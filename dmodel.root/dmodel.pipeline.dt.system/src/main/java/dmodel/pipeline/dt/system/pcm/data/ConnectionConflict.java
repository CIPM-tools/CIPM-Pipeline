package dmodel.pipeline.dt.system.pcm.data;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RequiredRole;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConnectionConflict extends AbstractConflict<ProvidedRole> {
	@EqualsAndHashCode.Exclude
	private List<ProvidedRole> provided;

	@EqualsAndHashCode.Exclude
	private RequiredRole required;

	private long id;

	public ConnectionConflict(long id) {
		super();
		this.provided = new ArrayList<>();
		this.id = id;
	}
}
