package dmodel.designtime.instrumentation.mapping.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BranchMappingComment extends AbstractMappingComment {

	@Getter
	private String branchTransitionId;

	@Override
	public boolean isBranchMapping() {
		return true;
	}

	@Override
	public BranchMappingComment asBranchMappingComment() {
		return this;
	}

}
