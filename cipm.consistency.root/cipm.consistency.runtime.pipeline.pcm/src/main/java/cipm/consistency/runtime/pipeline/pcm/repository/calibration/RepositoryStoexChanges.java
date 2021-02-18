package cipm.consistency.runtime.pipeline.pcm.repository.calibration;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.seff.AbstractInternalControlFlowAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.GuardedBranchTransition;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;
import org.palladiosimulator.pcm.seff.seff_performance.SeffPerformanceFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.pcm.distribution.DoubleDistribution;
import cipm.consistency.base.shared.pcm.util.PCMUtils;
import lombok.extern.java.Log;

@Log
public class RepositoryStoexChanges {
	private static final String CPU_RESOURCE_ID = "_oro4gG3fEdy4YaaT-RYrLQ";
	private static final String INTERNAL_ACTION_ADJUSTMENT_KEY = "-CD-ADJUST";

	private static final Pattern DISTRIBUTION_PATTERN = Pattern.compile("\\((.*?);(.*?)\\)");

	private Map<String, PCMRandomVariable> seffStoexChanges;
	private Map<String, PCMRandomVariable> seffConstantChanges;

	public RepositoryStoexChanges() {
		this.seffStoexChanges = Maps.newHashMap();
		this.seffConstantChanges = Maps.newHashMap();
	}

	public void inherit(RepositoryStoexChanges other) {
		other.seffStoexChanges.entrySet().forEach(e -> seffStoexChanges.put(e.getKey(), e.getValue()));
		other.seffConstantChanges.entrySet().forEach(e -> seffConstantChanges.put(e.getKey(), e.getValue()));
	}

	public void put(String id, PCMRandomVariable nStoex) {
		seffStoexChanges.put(id, nStoex);
	}

	public void putConstant(String id, PCMRandomVariable nStoex) {
		seffConstantChanges.put(id, nStoex);
	}

	public void clear() {
		seffStoexChanges.clear();
		seffConstantChanges.clear();
	}

	public Map<String, PCMRandomVariable> getStoexChanges() {
		return this.seffStoexChanges;
	}

	public Map<String, PCMRandomVariable> getConstantChanges() {
		return this.seffConstantChanges;
	}

	public void apply(Repository repository) {
		applyStoexChanges(repository);
		applyConstantChanges(repository);
	}

	public int size() {
		return seffStoexChanges.size();
	}

	private void applyStoexChanges(Repository repository) {
		ModelUtil.getObjects(repository, AbstractInternalControlFlowAction.class).stream().forEach(action -> {
			if (seffStoexChanges.containsKey(action.getId())) {
				PCMRandomVariable nStoex = seffStoexChanges.get(action.getId());
				if (action instanceof LoopAction) {
					((LoopAction) action).setIterationCount_LoopAction(nStoex);
				} else if (action instanceof GuardedBranchTransition) {
					((GuardedBranchTransition) action).setBranchCondition_GuardedBranchTransition(nStoex);
				} else if (action instanceof InternalAction) {
					if (action.getResourceDemand_Action().size() == 1) {
						action.getResourceDemand_Action().get(0).setSpecification_ParametericResourceDemand(nStoex);
					}
				} else if (action instanceof ExternalCallAction) {
					// TODO
				}
			}
		});
	}

	private void applyConstantChanges(Repository repository) {
		seffConstantChanges.entrySet().forEach(e -> {
			// search for corresponding internal action
			InternalAction adjustmentIA = PCMUtils.getElementById(repository, InternalAction.class,
					e.getKey() + INTERNAL_ACTION_ADJUSTMENT_KEY);
			if (adjustmentIA == null) {
				ResourceDemandingSEFF seff = PCMUtils.getElementById(repository, ResourceDemandingSEFF.class,
						e.getKey());
				createInternalAction(seff, e.getKey() + INTERNAL_ACTION_ADJUSTMENT_KEY, e.getValue());
			} else {
				adjustmentIA.getResourceDemand_Action().get(0).setSpecification_ParametericResourceDemand(
						sumUpDistributions(adjustmentIA.getResourceDemand_Action().get(0)
								.getSpecification_ParametericResourceDemand(), e.getValue()));
			}
		});
	}

	private PCMRandomVariable sumUpDistributions(PCMRandomVariable distr1, PCMRandomVariable distr2) {
		List<Pair<Double, Double>> pairs1 = parseDoubleDistribution(distr1);
		List<Pair<Double, Double>> pairs2 = parseDoubleDistribution(distr2);

		double average2 = pairs2.stream().mapToDouble(p -> p.getLeft() * p.getRight()).sum();

		DoubleDistribution finalDistr = new DoubleDistribution(5);
		pairs1.forEach(p -> {
			finalDistr.put(p.getLeft() + average2);
		});

		return finalDistr.toStoex();
	}

	private List<Pair<Double, Double>> parseDoubleDistribution(PCMRandomVariable distr1) {
		Matcher matcher1 = DISTRIBUTION_PATTERN.matcher(distr1.getSpecification());
		List<Pair<Double, Double>> pairs1 = Lists.newArrayList();

		while (matcher1.find()) {
			if (matcher1.groupCount() >= 2) {
				try {
					double d1 = Double.parseDouble(matcher1.group(1));
					double d2 = Double.parseDouble(matcher1.group(2));

					pairs1.add(Pair.of(d1, d2));
				} catch (NumberFormatException e) {
					log.warning("Failed to parse distribution: " + distr1.getSpecification());
				}
			}
		}

		if (pairs1.size() == 0) {
			// literal
			double doubleLiteral = Double.parseDouble(distr1.getSpecification());
			pairs1.add(Pair.of(doubleLiteral, 1.0d));
		}

		return pairs1;
	}

	private InternalAction createInternalAction(ResourceDemandingSEFF seff, String id, PCMRandomVariable demand) {
		InternalAction nAction = SeffFactory.eINSTANCE.createInternalAction();
		nAction.setId(id);
		nAction.setEntityName("Constant Determination dModel");

		StartAction start = ModelUtil.getObjects(seff, StartAction.class).get(0);
		nAction.setSuccessor_AbstractAction(start.getSuccessor_AbstractAction());
		nAction.setPredecessor_AbstractAction(start);

		ParametricResourceDemand nDemand = SeffPerformanceFactory.eINSTANCE.createParametricResourceDemand();
		nDemand.setSpecification_ParametericResourceDemand(demand);
		nDemand.setRequiredResource_ParametricResourceDemand(PCMUtils.getElementById(
				PCMUtils.getDefaultResourceRepository(), ProcessingResourceType.class, CPU_RESOURCE_ID));
		nAction.getResourceDemand_Action().add(nDemand);

		seff.getSteps_Behaviour().add(nAction);

		return nAction;
	}

}
