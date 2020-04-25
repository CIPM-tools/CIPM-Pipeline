package dmodel.runtime.pipelinepcm.usagemodel.data;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import com.google.common.collect.Lists;

import dmodel.base.shared.pcm.util.PCMUtils;
import dmodel.runtime.pipelinepcm.usagemodel.util.UsageServiceUtil;
import lombok.Data;

@Data
public class UsageGroup implements IPCMAnalogue<UsageScenario> {
	// smaller than this crashes the simulator in a lot of cases due to memory
	// problems
	private static final double MIN_INTERARRIVAL_TIME = 100;

	private List<IAbstractUsageDescriptor> descriptors;
	private int id;
	private double interarrivalTime;

	public UsageGroup(int id, double interarrival) {
		this.descriptors = Lists.newArrayList();
		this.id = id;
		this.interarrivalTime = interarrival;
	}

	@Override
	public UsageScenario toPCM() {
		UsageScenario ret = UsagemodelFactory.eINSTANCE.createUsageScenario();
		ret.setScenarioBehaviour_UsageScenario(UsageServiceUtil.createBehaviour(descriptors));
		ret.setEntityName("Scenario" + id);

		OpenWorkload workload = UsagemodelFactory.eINSTANCE.createOpenWorkload();
		workload.setInterArrivalTime_OpenWorkload(PCMUtils
				.createRandomVariableFromString(String.valueOf(Math.max(interarrivalTime, MIN_INTERARRIVAL_TIME))));
		ret.setWorkload_UsageScenario(workload);

		return ret;
	}
}
