package dmodel.pipeline.dt.system.impl;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphFactory;
import dmodel.pipeline.dt.system.ISystemCompositionAnalyzer;
import dmodel.pipeline.dt.system.util.SpoonUtil;
import dmodel.pipeline.monitoring.util.ManualMapping;
import dmodel.pipeline.records.instrument.spoon.SpoonCorrespondence;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
import lombok.extern.java.Log;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

@Component
@Log
public class StaticCodeReferenceAnalyzer implements ISystemCompositionAnalyzer {
	public StaticCodeReferenceAnalyzer() {
	}

	@Override
	public SpoonCorrespondence resolveManualMapping(Repository repository, Launcher model) {
		SpoonCorrespondence correspondenceOutput = new SpoonCorrespondence(model.getModel(), repository);

		model.getModel().filterChildren(new TypeFilter<CtMethod<?>>(CtMethod.class)).list().forEach(method -> {
			if (method instanceof CtMethod<?>) {
				CtMethod<?> currentMethod = (CtMethod<?>) method;
				if (currentMethod.hasAnnotation(ManualMapping.class)) {
					ManualMapping mapping = currentMethod.getAnnotation(ManualMapping.class);
					ResourceDemandingSEFF belSeff = PCMUtils.getElementById(repository, ResourceDemandingSEFF.class,
							mapping.value());

					if (belSeff != null) {
						correspondenceOutput.linkService(currentMethod, belSeff);
					} else {
						log.warning("Service with ID '" + belSeff.getId() + "' could not be resolved.");
					}
				}
			}
		});
		return correspondenceOutput;
	}

	@Override
	public ServiceCallGraph deriveSystemComposition(Launcher model, SpoonCorrespondence spoonCorr) {
		ServiceCallGraph serviceCallGraph = ServiceCallGraphFactory.eINSTANCE.createServiceCallGraph();

		spoonCorr.getServiceMappingEntries().stream().forEach(service -> {
			investigateService(service, serviceCallGraph, model, spoonCorr);
		});

		return serviceCallGraph;
	}

	private void investigateService(Entry<CtMethod<?>, ResourceDemandingSEFF> service, ServiceCallGraph callGraph,
			Launcher model, SpoonCorrespondence spoonCorr) {
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
				List<ResourceDemandingSEFF> nLinks = Collections.emptyList();
				if (resolvedMethod != null) {
					nLinks = resolveCalledSeffs(resolvedMethod, spoonCorr);
				} else if (classPathReference != null) {
					nLinks = resolveCalledSeffs(classPathReference, model, spoonCorr);
				}

				// create links
				nLinks.forEach(link -> {
					callGraph.incrementEdge(service.getValue(), link, null, null);
				});
			}
		});

	}

	private List<ResourceDemandingSEFF> resolveCalledSeffs(Method classPathReference, Launcher model,
			SpoonCorrespondence spoonCorr) {
		List<CtMethod<?>> matchingMethods = getMatchingMethods(classPathReference, model.getFactory(), spoonCorr);
		return matchingMethods.stream().map(m -> spoonCorr.resolveService(m)).collect(Collectors.toList());
	}

	private List<ResourceDemandingSEFF> resolveCalledSeffs(CtMethod<?> resolvedMethod, SpoonCorrespondence spoonCorr) {
		ResourceDemandingSEFF belongingSeff = spoonCorr.resolveService(resolvedMethod);
		if (belongingSeff != null) {
			// this is an easy case, we link the both services with a safe call
			return Lists.newArrayList(belongingSeff);
		} else {
			List<CtMethod<?>> matchingMethods = getMatchingMethods(resolvedMethod, spoonCorr);
			return matchingMethods.stream().map(m -> spoonCorr.resolveService(m)).collect(Collectors.toList());
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

}
