package dmodel.pipeline.rt.pcm.usagemodel;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Branch;
import org.palladiosimulator.pcm.usagemodel.BranchTransition;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import dmodel.pipeline.rt.pcm.usagemodel.cluster.SessionCluster;
import dmodel.pipeline.rt.pcm.usagemodel.data.usage.AbstractUsageElement;
import dmodel.pipeline.rt.pcm.usagemodel.mapping.MonitoringDataMapping;
import dmodel.pipeline.rt.pcm.usagemodel.util.ServiceCallUtil;

public class UsageScenarioBehaviourBuilder {
	private static final long NANO_TO_MS = 1000L * 1000L;

	private Repository repository;
	private System system;
	private MonitoringDataMapping mapping;

	public UsageScenarioBehaviourBuilder(System system, Repository repository, MonitoringDataMapping mapping) {
		this.repository = repository;
		this.system = system;
		this.mapping = mapping;
	}

	public UsageModel buildFullUsagemodel(List<SessionCluster> clusters) {
		UsageModel model = UsagemodelFactory.eINSTANCE.createUsageModel();
		UsageScenario scenario = UsagemodelFactory.eINSTANCE.createUsageScenario();

		Pair<ScenarioBehaviour, OpenWorkload> data = this.combineClusters(clusters);
		scenario.setScenarioBehaviour_UsageScenario(data.getLeft());
		scenario.setWorkload_UsageScenario(data.getRight());

		model.getUsageScenario_UsageModel().add(scenario);

		return model;
	}

	public UsageScenario buildUsageScenario(SessionCluster cluster) {
		UsageScenario scenario = UsagemodelFactory.eINSTANCE.createUsageScenario();

		OpenWorkload workload = UsagemodelFactory.eINSTANCE.createOpenWorkload();
		workload.setInterArrivalTime_OpenWorkload(
				ServiceCallUtil.buildLongLiteral(cluster.getInterArrivalTimeAverage() / NANO_TO_MS));

		scenario.setScenarioBehaviour_UsageScenario(buildBehaviour(cluster));
		scenario.setWorkload_UsageScenario(workload);

		return scenario;
	}

	public Pair<ScenarioBehaviour, OpenWorkload> combineClusters(List<SessionCluster> clusters) {
		ScenarioBehaviour back = UsagemodelFactory.eINSTANCE.createScenarioBehaviour();
		OpenWorkload workload = UsagemodelFactory.eINSTANCE.createOpenWorkload();

		// create fixed elements
		Start start = UsagemodelFactory.eINSTANCE.createStart();
		Branch branch = UsagemodelFactory.eINSTANCE.createBranch();
		Stop stop = UsagemodelFactory.eINSTANCE.createStop();

		back.getActions_ScenarioBehaviour().add(start);
		back.getActions_ScenarioBehaviour().add(stop);
		back.getActions_ScenarioBehaviour().add(branch);

		// chaining
		start.setSuccessor(branch);
		branch.setPredecessor(start);
		branch.setSuccessor(stop);
		stop.setPredecessor(branch);

		// preprocessing
		long highestInterarrival = clusters.stream().mapToLong(cl -> cl.getInterArrivalTimeAverage()).max().orElse(0);
		double executions = 0;
		for (SessionCluster cluster : clusters) {
			executions += highestInterarrival / (double) cluster.getInterArrivalTimeAverage();
		}

		// calculate interarrival time
		long interArrivalTime = (long) Math.floor(highestInterarrival / NANO_TO_MS / executions);
		PCMRandomVariable interArrival = ServiceCallUtil.buildLongLiteral(interArrivalTime);
		workload.setInterArrivalTime_OpenWorkload(interArrival);

		// if only one branch
		if (clusters.size() == 1) {
			return Pair.of(buildBehaviour(clusters.get(0)), workload);
		}

		// build branches
		for (SessionCluster cluster : clusters) {
			double clusterExecutions = highestInterarrival / (double) cluster.getInterArrivalTimeAverage();
			double branchProbability = clusterExecutions / executions;

			BranchTransition transition = UsagemodelFactory.eINSTANCE.createBranchTransition();
			transition.setBranchedBehaviour_BranchTransition(this.buildBehaviour(cluster));
			transition.setBranchProbability(branchProbability);

			branch.getBranchTransitions_Branch().add(transition);
		}

		return Pair.of(back, workload);
	}

	public ScenarioBehaviour buildBehaviour(SessionCluster cluster) {
		return this.buildBehaviour(cluster.getElements());
	}

	public ScenarioBehaviour buildBehaviour(List<AbstractUsageElement> structure) {
		ScenarioBehaviour back = UsagemodelFactory.eINSTANCE.createScenarioBehaviour();

		Start start = UsagemodelFactory.eINSTANCE.createStart();
		AbstractUserAction current = start;
		back.getActions_ScenarioBehaviour().add(start);

		for (AbstractUsageElement element : structure) {
			AbstractUserAction nAction = element.toUserAction(system, repository, mapping);
			back.getActions_ScenarioBehaviour().add(nAction);

			current.setSuccessor(nAction);
			nAction.setPredecessor(current);
			current = nAction;
		}

		Stop stop = UsagemodelFactory.eINSTANCE.createStop();
		back.getActions_ScenarioBehaviour().add(stop);
		stop.setPredecessor(current);
		current.setSuccessor(stop);

		return back;
	}

}
