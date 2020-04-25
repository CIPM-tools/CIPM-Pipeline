package dmodel.designtime.instrumentation.transformation.impl.sub;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;

import dmodel.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;

public class ExternalCallInstrumenter extends SubInstrumentationHelper {

	public void instrument(Statement callStatement, String externalCallId) {
		// literals
		StringLiteralExpr callIdLiteral = new StringLiteralExpr(externalCallId);

		// method call
		ExpressionStmt callEnterExpr = new ExpressionStmt();
		MethodCallExpr setExternalCallExpr = new MethodCallExpr(threadMonitoringControllerReference,
				ApplicationProjectInstrumenterNamespace.METHOD_BEFORE_EXTERNAL_CALL, NodeList.nodeList(callIdLiteral));
		callEnterExpr.setExpression(setExternalCallExpr);

		addBefore(callStatement, callEnterExpr);
	}

}
