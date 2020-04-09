package dmodel.pipeline.instrumentation.transformation.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import dmodel.pipeline.instrumentation.project.ParsedApplicationProject;
import dmodel.pipeline.instrumentation.transformation.IApplicationProjectInstrumenter;
import dmodel.pipeline.instrumentation.transformation.impl.sub.BranchActionInstrumenter;
import dmodel.pipeline.instrumentation.transformation.impl.sub.InternalActionInstrumenter;
import dmodel.pipeline.instrumentation.transformation.impl.sub.LoopActionInstrumenter;
import dmodel.pipeline.instrumentation.transformation.impl.sub.ServiceCallInstrumenter;
import dmodel.pipeline.instrumentation.tuid.JavaTuidGeneratorAndResolver;
import dmodel.pipeline.monitoring.controller.ServiceParameters;
import dmodel.pipeline.monitoring.controller.ThreadMonitoringController;
import dmodel.pipeline.vsum.domains.java.IJavaPCMCorrespondenceModel;
import lombok.extern.java.Log;

@Log
public class ApplicationProjectInstrumenterImpl implements IApplicationProjectInstrumenter {
	private JavaTuidGeneratorAndResolver tuidResolver;
	private JavaParser javaParserUtil;

	private Set<MethodDeclaration> alreadyPreparedMethods;

	private ServiceCallInstrumenter serviceCallInstrumenter;
	private InternalActionInstrumenter internalActionInstrumenter;
	private LoopActionInstrumenter loopActionInstrumenter;
	private BranchActionInstrumenter branchActionInstrumenter;

	private Map<String, Statement> resolvedStatements;

	public ApplicationProjectInstrumenterImpl() {
		tuidResolver = new JavaTuidGeneratorAndResolver();
		javaParserUtil = new JavaParser();

		serviceCallInstrumenter = new ServiceCallInstrumenter();
		internalActionInstrumenter = new InternalActionInstrumenter();
		loopActionInstrumenter = new LoopActionInstrumenter();
		branchActionInstrumenter = new BranchActionInstrumenter();
	}

	@Override
	public void transform(ParsedApplicationProject pap, IJavaPCMCorrespondenceModel correspondence) {
		alreadyPreparedMethods = Sets.newHashSet();
		loopActionInstrumenter.prepare();

		resolveAllCorrespondences(pap, correspondence);

		correspondence.getSeffCorrespondences().forEach(corr -> instrumentService(pap, corr));
		correspondence.getInternalActionCorrespondences().forEach(corr -> instrumentInternalAction(pap, corr));
		correspondence.getBranchCorrespondences().forEach(corr -> instrumentBranchAction(pap, corr));
		correspondence.getLoopCorrespondences().forEach(corr -> instrumentLoopAction(pap, corr));
	}

	private void instrumentLoopAction(ParsedApplicationProject pap, Pair<String, String> corr) {
		Statement loopStatement = resolvedStatements.get(corr.getLeft());
		if (loopStatement == null) {
			log.warning("Loop statement could not be resolved.");
			return;
		}

		// instrument it
		loopActionInstrumenter.instrument(loopStatement, corr.getRight());

		// prepare parent
		MethodDeclaration methodParent = getMethodParent(loopStatement);
		prepareMethod(methodParent);
	}

	private void instrumentBranchAction(ParsedApplicationProject pap, Pair<String, String> corr) {
		Statement branchStatement = resolvedStatements.get(corr.getLeft());
		if (branchStatement == null) {
			log.warning("Branch statement could not be resolved.");
			return;
		}

		// instrument it
		branchActionInstrumenter.instrument(branchStatement, corr.getRight());

		// prepare parent
		MethodDeclaration methodParent = getMethodParent(branchStatement);
		prepareMethod(methodParent);
	}

	private void instrumentInternalAction(ParsedApplicationProject pap, Pair<Pair<String, String>, String> corr) {
		Statement startStatement = resolvedStatements.get(corr.getLeft().getLeft());
		Statement endStatement = resolvedStatements.get(corr.getLeft().getRight());
		if (startStatement == null || endStatement == null) {
			log.warning("Start or end statement of an internal action could not be found.");
			return;
		}

		// instrument it
		internalActionInstrumenter.instrumentService(startStatement, endStatement, corr.getRight());

		// prepare parents
		MethodDeclaration methodParentStart = getMethodParent(startStatement);
		MethodDeclaration methodParentEnd = getMethodParent(endStatement);

		prepareMethod(methodParentStart);
		prepareMethod(methodParentEnd);
	}

	private void instrumentService(ParsedApplicationProject pap, Pair<String, String> corr) {
		MethodDeclaration decl = tuidResolver.resolveMethod(pap, corr.getLeft());
		if (!decl.getBody().isPresent()) {
			return;
		}

		serviceCallInstrumenter.instrumentService(decl, corr.getRight());
		prepareMethod(decl);
	}

	private void resolveAllCorrespondences(ParsedApplicationProject pap, IJavaPCMCorrespondenceModel correspondence) {
		resolvedStatements = Maps.newHashMap();

		correspondence.getBranchCorrespondences()
				.forEach(b -> resolvedStatements.put(b.getKey(), tuidResolver.resolveStatement(pap, b.getKey())));
		correspondence.getLoopCorrespondences()
				.forEach(l -> resolvedStatements.put(l.getKey(), tuidResolver.resolveStatement(pap, l.getKey())));
		correspondence.getInternalActionCorrespondences().forEach(i -> {
			resolvedStatements.put(i.getKey().getLeft(), tuidResolver.resolveStatement(pap, i.getKey().getLeft()));
			resolvedStatements.put(i.getKey().getRight(), tuidResolver.resolveStatement(pap, i.getKey().getRight()));
		});
	}

	private void prepareMethod(MethodDeclaration method) {
		if (method != null && !alreadyPreparedMethods.contains(method)) {
			CompilationUnit parent = getCompilationUnit(method);

			// add imports
			parent.addImport(ThreadMonitoringController.class);
			parent.addImport(ServiceParameters.class);
			parent.addImport(AtomicInteger.class);

			// create var declaration
			ExpressionStmt varDecl = new ExpressionStmt();
			ClassOrInterfaceType threadMonitoringControllerDecl = javaParserUtil
					.parseClassOrInterfaceType(ThreadMonitoringController.class.getSimpleName()).getResult().get();
			TypeExpr threadMonitoringControllerType = new TypeExpr(threadMonitoringControllerDecl);
			VariableDeclarator declarator = new VariableDeclarator(threadMonitoringControllerDecl,
					ApplicationProjectInstrumenterNamespace.THREAD_MONITORING_CONTROLLER_VARIABLE);
			MethodCallExpr initExpr = new MethodCallExpr(threadMonitoringControllerType,
					ApplicationProjectInstrumenterNamespace.METHOD_GET_INSTANCE);
			declarator.setInitializer(initExpr);
			VariableDeclarationExpr varDeclExpr = new VariableDeclarationExpr(declarator);
			varDecl.setExpression(varDeclExpr);

			// add declaration
			method.getBody().get().addStatement(0, varDecl);

			alreadyPreparedMethods.add(method);
		}
	}

	private CompilationUnit getCompilationUnit(Node node) {
		return getParentOfType(node, CompilationUnit.class);
	}

	private MethodDeclaration getMethodParent(Node node) {
		return getParentOfType(node, MethodDeclaration.class);
	}

	private <T> T getParentOfType(Node node, Class<T> type) {
		while (node.getParentNode().isPresent()) {
			if (type.isInstance(node.getParentNode().get())) {
				return type.cast(node.getParentNode().get());
			}
			node = node.getParentNode().get();
		}
		return null;
	}

}
