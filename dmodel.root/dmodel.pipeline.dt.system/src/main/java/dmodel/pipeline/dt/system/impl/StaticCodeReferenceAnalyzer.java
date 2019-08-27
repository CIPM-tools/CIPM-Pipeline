package dmodel.pipeline.dt.system.impl;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import dmodel.pipeline.dt.system.ISystemCompositionAnalyzer;
import dmodel.pipeline.dt.system.util.SpoonUtil;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondence;
import dmodel.pipeline.shared.structure.DirectedGraph;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

@Component
public class StaticCodeReferenceAnalyzer implements ISystemCompositionAnalyzer {
	private static final Logger LOG = LoggerFactory.getLogger(StaticCodeReferenceAnalyzer.class);

	public StaticCodeReferenceAnalyzer() {
	}

	@Override
	public DirectedGraph<String, Integer> deriveSystemComposition(Launcher model, SpoonCorrespondence spoonCorr) {
		DirectedGraph<String, Integer> serviceCallGraph = new DirectedGraph<>();

		spoonCorr.getServiceMappingEntries().stream().forEach(service -> {
			investigateService(service, serviceCallGraph, model, spoonCorr);
		});

		return serviceCallGraph;
	}

	private void investigateService(Entry<CtMethod<?>, ResourceDemandingSEFF> service,
			DirectedGraph<String, Integer> callGraph, Launcher model, SpoonCorrespondence spoonCorr) {
		// get method and code block for the method
		CtMethod<?> belongingMethod = service.getKey();
		CtBlock<?> body = belongingMethod.getBody();

		// get all invocations
		body.filterChildren(new TypeFilter<CtInvocation<?>>(CtInvocation.class)).list().forEach(inv -> {
			if (inv instanceof CtInvocation<?>) {
				CtInvocation<?> invoc = (CtInvocation<?>) inv;

				// get belonging method in the spoon model (if it exists)
				CtMethod<?> resolvedMethod = SpoonUtil.getMethodFromExecutable(model.getModel(), invoc.getExecutable());

				// get belonging method on the classpath (if it exists there)
				Method classPathReference = SpoonUtil.getClassPathReferenceSoft(invoc.getExecutable());

				// if found in one of these
				List<Pair<String, String>> nLinks = Collections.emptyList();
				if (resolvedMethod != null) {
					nLinks = resolveCalledSeffs(service.getValue().getId(), resolvedMethod, spoonCorr);
				} else if (classPathReference != null) {
					nLinks = resolveCalledSeffs(service.getValue().getId(), classPathReference, model, spoonCorr);
				}

				// create links
				nLinks.forEach(link -> {
					this.incrementEdge(callGraph, link.getLeft(), link.getRight());
				});
			}
		});

	}

	private List<Pair<String, String>> resolveCalledSeffs(String id, Method classPathReference, Launcher model,
			SpoonCorrespondence spoonCorr) {
		List<CtMethod<?>> matchingMethods = getMatchingMethods(classPathReference, model.getFactory(), spoonCorr);
		return matchingMethods.stream().map(m -> Pair.of(id, spoonCorr.resolveService(m).getId()))
				.collect(Collectors.toList());
	}

	private List<Pair<String, String>> resolveCalledSeffs(String sourceServiceId, CtMethod<?> resolvedMethod,
			SpoonCorrespondence spoonCorr) {
		ResourceDemandingSEFF belongingSeff = spoonCorr.resolveService(resolvedMethod);
		if (belongingSeff != null) {
			// this is an easy case, we link the both services with a safe call
			return Lists.newArrayList(Pair.of(sourceServiceId, belongingSeff.getId()));
		} else {
			List<CtMethod<?>> matchingMethods = getMatchingMethods(resolvedMethod, spoonCorr);
			return matchingMethods.stream().map(m -> Pair.of(sourceServiceId, spoonCorr.resolveService(m).getId()))
					.collect(Collectors.toList());
		}
	}

	private List<CtMethod<?>> getMatchingMethods(Method classPathReference, Factory spoonFactory,
			SpoonCorrespondence corr) {
		return corr.getServiceMappingEntries().stream().filter(entry -> {
			CtType<?> enclosingType = entry.getKey().getDeclaringType();
			if (enclosingType
					.isSubtypeOf(spoonFactory.createReference(classPathReference.getDeclaringClass().getName()))) {
				return SpoonUtil.executableReferencesEqual(classPathReference, entry.getKey().getReference());
			}

			return false;
		}).map(e -> e.getKey()).collect(Collectors.toList());
	}

	private List<CtMethod<?>> getMatchingMethods(CtMethod<?> method, SpoonCorrespondence corr) {
		return corr.getServiceMappingEntries().stream().filter(entry -> {
			CtType<?> enclosingType = entry.getKey().getDeclaringType();
			if (enclosingType.isSubtypeOf(method.getDeclaringType().getReference())) {
				return SpoonUtil.executableReferencesEqual(method.getReference(), entry.getKey().getReference());
			}

			return false;
		}).map(e -> e.getKey()).collect(Collectors.toList());
	}

	private <T> void incrementEdge(DirectedGraph<T, Integer> graph, T n1, T n2) {
		if (graph.hasEdge(n1, n2)) {
			int value = graph.getEdge(n1, n2);
			graph.modifyEdge(n1, n2, value + 1);
		}
		graph.addEdge(n1, n2, 1);
	}

}
