package dmodel.designtime.instrumentation.transformation.impl.sub;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;

import dmodel.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;

public class InternalActionInstrumenter extends SubInstrumentationHelper {
	public void instrumentService(Statement start, Statement end, String correspondingInternalActionId) {
		// literals
		StringLiteralExpr internalActionIdLiteral = new StringLiteralExpr(correspondingInternalActionId);
		StringLiteralExpr cpuResourceIdLiteral = new StringLiteralExpr(
				ApplicationProjectInstrumenterNamespace.RESOURCE_ID_CPU);

		// entry method call
		ExpressionStmt entryMethodCallExpr = new ExpressionStmt();
		MethodCallExpr entryMethodCall = new MethodCallExpr(threadMonitoringControllerReference,
				ApplicationProjectInstrumenterNamespace.METHOD_ENTER_INTERNAL_ACTION,
				NodeList.nodeList(internalActionIdLiteral, cpuResourceIdLiteral));
		entryMethodCallExpr.setExpression(entryMethodCall);

		// exit method call
		ExpressionStmt exitMethodCallExpr = new ExpressionStmt();
		MethodCallExpr exitMethodCall = new MethodCallExpr(threadMonitoringControllerReference,
				ApplicationProjectInstrumenterNamespace.METHOD_EXIT_INTERNAL_ACTION,
				NodeList.nodeList(internalActionIdLiteral, cpuResourceIdLiteral));
		exitMethodCallExpr.setExpression(exitMethodCall);

		// instrumentation
		this.addBefore(start, entryMethodCallExpr);
		this.addAfter(end, exitMethodCallExpr);
	}

}
