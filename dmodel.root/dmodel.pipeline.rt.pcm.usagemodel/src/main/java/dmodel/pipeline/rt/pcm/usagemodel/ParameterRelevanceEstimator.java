package dmodel.pipeline.rt.pcm.usagemodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.parameter.CharacterisedVariable;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import de.uka.ipd.sdq.stoex.CompareExpression;
import de.uka.ipd.sdq.stoex.Expression;
import de.uka.ipd.sdq.stoex.ProductExpression;
import dmodel.pipeline.shared.ModelUtil;

public class ParameterRelevanceEstimator {

	public Set<Parameter> getRelevantParameters(ResourceDemandingSEFF seff, float thres) {
		return estimateDependencies(seff).stream().filter(pair -> pair.getRight() >= thres).map(pair -> pair.getLeft())
				.collect(Collectors.toSet());
	}

	public List<Pair<Parameter, Float>> estimateDependencies(ResourceDemandingSEFF seff) {
		if (seff.getDescribedService__SEFF() instanceof OperationSignature) {
			// get signature
			OperationSignature opSig = (OperationSignature) seff.getDescribedService__SEFF();

			// get parameters
			Set<Parameter> effParameters = new HashSet<>(opSig.getParameters__OperationSignature());

			// dependencies
			Map<Parameter, Integer> hits = new HashMap<>();

			// get random variables
			float vars = 0;
			for (AbstractAction action : seff.getSteps_Behaviour()) {
				for (PCMRandomVariable var : ModelUtil.getObjects(action, PCMRandomVariable.class)) {
					for (Parameter para : inspectRandomVariable(var, effParameters)) {
						if (!hits.containsKey(para)) {
							hits.put(para, 0);
						}
						hits.put(para, hits.get(para) + 1);
					}
					vars++;
				}
			}

			// loop entries
			final float varSum = vars;
			return hits.entrySet().stream().map(entry -> {
				return Pair.of(entry.getKey(), entry.getValue() / varSum);
			}).collect(Collectors.toList());
		}
		return null;
	}

	private Set<Parameter> inspectRandomVariable(PCMRandomVariable var, Set<Parameter> parameters) {
		return inspectExpression(var.getExpression(), parameters);
	}

	private Set<Parameter> inspectExpression(Expression exp, Set<Parameter> parameters) {
		if (exp instanceof CharacterisedVariable) {
			String dependsOn = (((CharacterisedVariable) exp).getId_Variable().getReferenceName());
			return parameters.stream().filter(para -> para.getParameterName().equals(dependsOn))
					.collect(Collectors.toSet());
		} else if (exp instanceof ProductExpression) {
			return Stream
					.concat(inspectExpression(((ProductExpression) exp).getLeft(), parameters).stream(),
							inspectExpression(((ProductExpression) exp).getRight(), parameters).stream())
					.collect(Collectors.toSet());
		} else if (exp instanceof CompareExpression) {
			return Stream
					.concat(inspectExpression(((CompareExpression) exp).getLeft(), parameters).stream(),
							inspectExpression(((CompareExpression) exp).getRight(), parameters).stream())
					.collect(Collectors.toSet());
		} else {
			// System.out.println(exp.getClass().getName());
		}
		return Collections.emptySet();
	}

}
