package dmodel.pipeline.instrumentation.transformation.impl.sub;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;

import dmodel.pipeline.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;

public class BranchActionInstrumenter extends SubInstrumentationHelper {

	public void instrument(Statement branchStatement, String transitionId) {
		// literals
		StringLiteralExpr transitionIdLiteral = new StringLiteralExpr(transitionId);

		// method call
		ExpressionStmt branchEnterExpr = new ExpressionStmt();
		MethodCallExpr branchEnterCall = new MethodCallExpr(threadMonitoringControllerReference,
				ApplicationProjectInstrumenterNamespace.METHOD_ENTER_BRANCH, NodeList.nodeList(transitionIdLiteral));
		branchEnterExpr.setExpression(branchEnterCall);

		if (branchStatement instanceof BlockStmt) {
			((BlockStmt) branchStatement).addStatement(0, branchEnterExpr);
		}
	}

}
