package dmodel.designtime.instrumentation.mapping.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ExternalCallMappingComment extends AbstractMappingComment {

	@Getter
	private String externalCallId;

	public ExternalCallMappingComment asExternalCallMappingComment() {
		return this;
	}

	public boolean isExternalCallMapping() {
		return true;
	}

}
