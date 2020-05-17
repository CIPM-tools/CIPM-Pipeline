package dmodel.designtime.system.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.springframework.stereotype.Component;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.resolution.SymbolResolver;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import dmodel.base.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphFactory;
import dmodel.base.shared.pcm.util.repository.PCMRepositoryUtil;
import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;
import dmodel.designtime.instrumentation.tuid.JavaTuidGeneratorAndResolver;
import dmodel.designtime.system.ISystemCompositionAnalyzer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;

@Component
@Log
public class StaticCodeReferenceAnalyzer implements ISystemCompositionAnalyzer {
	private JavaTuidGeneratorAndResolver tuidResolver;

	public StaticCodeReferenceAnalyzer() {
		this.tuidResolver = new JavaTuidGeneratorAndResolver();
	}

	@Override
	public ServiceCallGraph deriveSystemComposition(ParsedApplicationProject parsedApplication,
			List<File> binaryJarFiles, IRepositoryQueryFacade repository, IJavaPCMCorrespondenceModel correspondence) {
		parsedApplication.reparse(); // reparse whole project
		ServiceCallGraph output = ServiceCallGraphFactory.eINSTANCE.createServiceCallGraph();

		CombinedTypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
		for (File jarFile : binaryJarFiles) {
			try {
				typeSolver.add(new JarTypeSolver(jarFile));
			} catch (IOException e) {
				log.warning("Failed to resolve JAR '" + jarFile.getAbsolutePath() + "'.");
			}
		}
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);

		AnalyzerData analyzerData = new AnalyzerData(parsedApplication, typeSolver, symbolSolver, output, repository,
				correspondence);

		correspondence.getExternalCallCorrespondences().forEach(extCall -> {
			Statement stmt = tuidResolver.resolveStatement(parsedApplication, extCall.getLeft());
			ExternalCallAction ext = repository.getElementById(extCall.getRight(), ExternalCallAction.class);
			ResourceDemandingSEFF seff = PCMRepositoryUtil.getParentService(ext);

			if (stmt != null) {
				List<MethodCallExpr> calls = stmt.findAll(MethodCallExpr.class);
				calls.forEach(call -> inspectMethodCall(analyzerData, call, ext, seff));
			} else {
				log.warning("Failed to resolve Java statement: '" + extCall.getLeft() + "'.");
			}
		});

		return output;
	}

	private void inspectMethodCall(AnalyzerData data, MethodCallExpr methodCall, ExternalCallAction externalCallAction,
			ResourceDemandingSEFF source) {
		for (Pair<String, String> seffCorrespondence : data.cpm.getSeffCorrespondences()) {
			MethodDeclaration correspondingMethod = this.tuidResolver.resolveMethod(data.pap,
					seffCorrespondence.getLeft());
			if (methodConformsTo(data, correspondingMethod, methodCall)) {
				ResourceDemandingSEFF target = data.repository.getServiceById(seffCorrespondence.getRight());
				data.graph.incrementEdge(source, target, null, null, externalCallAction);
			}
		}
	}

	private boolean methodConformsTo(AnalyzerData data, MethodDeclaration correspondingMethod,
			MethodCallExpr methodCall) {
		if (methodCall.getScope().isPresent()) {
			if (correspondingMethod.getNameAsString().equals(methodCall.getNameAsString())) {
				Expression scope = methodCall.getScope().get();

				ResolvedType scopeType = JavaParserFacade.get(data.solver).getType(scope);
				SymbolReference<ResolvedMethodDeclaration> methodDefReference = JavaParserFacade.get(data.solver)
						.solve(methodCall);

				ResolvedType declarationType = JavaParserFacade.get(data.solver).getTypeOfThisIn(correspondingMethod);

				boolean subType = isSubtypeOf(declarationType, scopeType);
				boolean methodsEqual = methodSignatureEqual(
						data.symbolSolver.resolveDeclaration(correspondingMethod, ResolvedMethodDeclaration.class),
						methodDefReference.getCorrespondingDeclaration());

				return subType && methodsEqual;
			}
		}

		return false;
	}

	private boolean methodSignatureEqual(ResolvedMethodDeclaration meth1, ResolvedMethodDeclaration meth2) {
		return meth1.getQualifiedSignature().equals(meth1.getQualifiedSignature());
	}

	private boolean isSubtypeOf(ResolvedType target, ResolvedType parent) {
		return parent.isAssignableBy(target);
	}

	@Data
	@AllArgsConstructor
	private class AnalyzerData {
		ParsedApplicationProject pap;
		TypeSolver solver;
		SymbolResolver symbolSolver;
		ServiceCallGraph graph;
		IRepositoryQueryFacade repository;
		IJavaPCMCorrespondenceModel cpm;
	}

}
