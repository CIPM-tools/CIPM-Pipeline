package dmodel.runtime.pipeline.pcm.resourceenv.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class EnvironmentData {
	private List<Host> hosts;
	private List<HostLink> connections;

	public EnvironmentData() {
		hosts = new ArrayList<>();
		connections = new ArrayList<>();
	}
}
