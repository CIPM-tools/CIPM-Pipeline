package cipm.consistency.designtime.instrumentation.mapping.comment;

public abstract class AbstractMappingComment {

	public boolean isInternalActionMapping() {
		return false;
	}

	public boolean isLoopMapping() {
		return false;
	}

	public boolean isBranchMapping() {
		return false;
	}

	public boolean isExternalCallMapping() {
		return false;
	}

	public InternalActionMappingComment asInternalActionMappingComment() {
		return null;
	}

	public LoopMappingComment asLoopMappingComment() {
		return null;
	}

	public BranchMappingComment asBranchMappingComment() {
		return null;
	}

	public ExternalCallMappingComment asExternalCallMappingComment() {
		return null;
	}

}
