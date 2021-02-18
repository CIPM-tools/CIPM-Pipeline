package cipm.consistency.runtime.pipeline.pcm.resourceenv.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Host {
	private String id;
	private String name;
}
