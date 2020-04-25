package dmodel.runtime.pipeline.pcm.resourceenv.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HostLink {
	String fromId;
	String toId;
}
