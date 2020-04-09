package dmodel.pipeline.instrumentation.transformation.impl.sub;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.TryStmt;

import dmodel.pipeline.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;

public class ServiceCallInstrumenter extends SubInstrumentationHelper {

	public void instrumentService(MethodDeclaration decl, String correspondingSeffId) {
		BlockStmt nParent = new BlockStmt();

		// literal
		StringLiteralExpr seffIdLiteral = new StringLiteralExpr(correspondingSeffId);

		// generate try catch clause
		TryStmt nTry = new TryStmt();
		nTry.setTryBlock(decl.getBody().get());

		// create variable parameters
		ExpressionStmt varDeclParameters = new ExpressionStmt();
		VariableDeclarator declarator = new VariableDeclarator(serviceParametersType,
				ApplicationProjectInstrumenterNamespace.SERVICE_PARAMETERS_VARIABLE);
		ObjectCreationExpr creation = new ObjectCreationExpr(null, serviceParametersType, NodeList.nodeList());
		declarator.setInitializer(creation);
		varDeclParameters.setExpression(new VariableDeclarationExpr(declarator));

		nParent.addStatement(varDeclParameters);

		// add parameter values
		for (Parameter parameter : decl.getParameters()) {
			StringLiteralExpr parameterNameLiteral = new StringLiteralExpr(parameter.getNameAsString());
			NameExpr parameterReference = new NameExpr(parameter.getNameAsString());

			MethodCallExpr addParameterExpr = new MethodCallExpr(serviceParametersReference,
					ApplicationProjectInstrumenterNamespace.METHOD_ADD_PARAMETER_VALUE,
					NodeList.nodeList(parameterNameLiteral, parameterReference));
			nParent.addStatement(addParameterExpr);
		}

		// create enter statement
		ExpressionStmt enterServiceStmt = new ExpressionStmt();
		MethodCallExpr enterServiceCall = new MethodCallExpr(threadMonitoringControllerReference,
				ApplicationProjectInstrumenterNamespace.METHOD_ENTER_SERVICE,
				NodeList.nodeList(seffIdLiteral, new ThisExpr(), serviceParametersReference));
		enterServiceStmt.setExpression(enterServiceCall);
		nParent.addStatement(enterServiceStmt);

		// create finally block
		BlockStmt nFinally = new BlockStmt();

		ExpressionStmt exitServiceStmt = new ExpressionStmt();
		exitServiceStmt.setExpression(new MethodCallExpr(threadMonitoringControllerReference,
				ApplicationProjectInstrumenterNamespace.METHOD_EXIT_SERVICE, new NodeList<Expression>(seffIdLiteral)));
		nFinally.addStatement(exitServiceStmt);
		nTry.setFinallyBlock(nFinally);

		// exchange body
		nParent.addStatement(nTry);
		decl.setBody(nParent);
	}

}
