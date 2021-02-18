package cipm.consistency.designtime.instrumentation.mapping.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class InternalActionMappingComment extends AbstractMappingComment {
	@Getter
	private boolean start;

	@Getter
	private String internalActionId;

	@Override
	public boolean isInternalActionMapping() {
		return true;
	}

	@Override
	public InternalActionMappingComment asInternalActionMappingComment() {
		return this;
	}

}
