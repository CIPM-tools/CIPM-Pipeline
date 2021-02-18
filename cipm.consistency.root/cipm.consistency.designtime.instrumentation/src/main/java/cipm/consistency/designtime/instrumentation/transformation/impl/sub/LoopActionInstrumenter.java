package cipm.consistency.designtime.instrumentation.transformation.impl.sub;

import java.util.Map;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.google.common.collect.Maps;

import cipm.consistency.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;

public class LoopActionInstrumenter extends SubInstrumentationHelper {
	private Map<MethodDeclaration, Integer> alreadyIntroducedCounters;

	public void prepare() {
		alreadyIntroducedCounters = Maps.newHashMap();
	}

	public void instrument(Statement loop, String loopId) {
		// literal
		StringLiteralExpr loopIdLiteral = new StringLiteralExpr(loopId);

		// prepare method
		MethodDeclaration methodParent = getParentOfType(loop, MethodDeclaration.class);
		int currentCounterId;
		if (alreadyIntroducedCounters.containsKey(methodParent)) {
			currentCounterId = alreadyIntroducedCounters.get(methodParent);
			alreadyIntroducedCounters.put(methodParent, currentCounterId + 1);
		} else {
			currentCounterId = 1;
			alreadyIntroducedCounters.put(methodParent, 2);
		}
		String counterName = ApplicationProjectInstrumenterNamespace.COUNTER_VARIABLE + currentCounterId;

		ExpressionStmt counterDeclarationStmt = new ExpressionStmt();
		VariableDeclarator counterDeclarator = new VariableDeclarator(atomicIntegerType, counterName);
		ObjectCreationExpr creation = new ObjectCreationExpr(null, atomicIntegerType,
				NodeList.nodeList(new IntegerLiteralExpr("0")));
		counterDeclarator.setInitializer(creation);

		VariableDeclarationExpr counterDeclaration = new VariableDeclarationExpr(counterDeclarator);

		// add counter declaration
		counterDeclarationStmt.setExpression(counterDeclaration);
		this.addBefore(loop, counterDeclarationStmt);

		// get corresponding block statement
		BlockStmt loopBlockStmt = getBlockStatement(loop);
		if (loopBlockStmt != null) {
			MethodCallExpr incrementStmt = new MethodCallExpr(new NameExpr(counterName),
					ApplicationProjectInstrumenterNamespace.METHOD_ATOMIC_INTEGER_INCREMENT, NodeList.nodeList());
			ExpressionStmt incrementExpr = new ExpressionStmt();
			incrementExpr.setExpression(incrementStmt);
			loopBlockStmt.addStatement(0, incrementExpr);
		}

		// add log statement
		ExpressionStmt logStmt = new ExpressionStmt();
		MethodCallExpr logCall = new MethodCallExpr(threadMonitoringControllerReference,
				ApplicationProjectInstrumenterNamespace.METHOD_EXIT_LOOP,
				NodeList.nodeList(loopIdLiteral, new MethodCallExpr(new NameExpr(counterName),
						ApplicationProjectInstrumenterNamespace.METHOD_ATOMIC_INTEGER_GET, NodeList.nodeList())));
		logStmt.setExpression(logCall);
		this.addAfter(loop, logStmt);
	}

	private BlockStmt getBlockStatement(Statement loop) {
		if (loop instanceof ForStmt) {
			if (((ForStmt) loop).getBody() instanceof BlockStmt) {
				return (BlockStmt) (((ForStmt) loop).getBody());
			}
		} else if (loop instanceof WhileStmt) {
			if (((WhileStmt) loop).getBody() instanceof BlockStmt) {
				return (BlockStmt) (((WhileStmt) loop).getBody());
			}
		} else if (loop instanceof DoStmt) {
			if (((DoStmt) loop).getBody() instanceof BlockStmt) {
				return (BlockStmt) (((DoStmt) loop).getBody());
			}
		} else if (loop instanceof ExpressionStmt) {
			return processLambda(loop);
		}

		return null;
	}

	private BlockStmt processLambda(Statement loop) {
		ExpressionStmt parentStmt = (ExpressionStmt) loop;
		if (parentStmt.getExpression() instanceof MethodCallExpr) {
			MethodCallExpr lambdaPrepareExpr = (MethodCallExpr) parentStmt.getExpression();
			if (lambdaPrepareExpr.getArguments().size() > 0 && lambdaPrepareExpr.getArgument(0) instanceof LambdaExpr) {
				LambdaExpr lambda = (LambdaExpr) lambdaPrepareExpr.getArgument(0);
				if (!(lambda.getBody() instanceof BlockStmt)) {
					BlockStmt bodyReplace = new BlockStmt();
					bodyReplace.addStatement(0, lambda.getBody());
					lambda.setBody(bodyReplace);
				}
				return (BlockStmt) lambda.getBody();
			}
		}

		return null;
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
