package cipm.consistency.designtime.instrumentation.mapping.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class LoopMappingComment extends AbstractMappingComment {

	@Getter
	private String loopId;

	@Override
	public LoopMappingComment asLoopMappingComment() {
		return this;
	}

	@Override
	public boolean isLoopMapping() {
		return true;
	}

}
