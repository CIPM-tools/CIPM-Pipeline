package dmodel.pipeline.rt.router;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.emf.ecore.util.EcoreUtil;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.records.ServiceContextRecord;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pipeline.PortIDs;
import dmodel.pipeline.shared.structure.Tree;
import lombok.extern.java.Log;

@Log
public class AccuracySwitch extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	private ScheduledExecutorService executorService;

	public AccuracySwitch() {
		executorService = Executors.newScheduledThreadPool(2);
	}

	@InputPorts({ @InputPort(PortIDs.T_SC_ROUTER), @InputPort(PortIDs.T_RAW_ROUTER),
			@InputPort(PortIDs.T_SYSTEM_ROUTER) })
	public void accuracyRouter(List<Tree<ServiceCallRecord>> entryCalls, List<ServiceContextRecord> rawMonitoringData) {
		log.info("Running usage model and repository derivation.");

		InMemoryPCM copyForUsage = getBlackboard().getArchitectureModel().copyReference();
		InMemoryPCM copyForRepository = getBlackboard().getArchitectureModel().copyReference();

		// we assume here that these two transformations only change one model part
		// for the usage transformation only the usage model is duplicated and for the
		// repository only the repo
		copyForUsage.setUsageModel(EcoreUtil.copy(copyForUsage.getUsageModel()));
		copyForRepository.setRepository(EcoreUtil.copy(copyForRepository.getRepository()));

		// invoke the transformations
		// TODO
		// TODO async
	}

}
