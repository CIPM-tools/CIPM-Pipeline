package cipm.consistency.designtime.instrumentation.transformation.impl.sub;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import cipm.consistency.bridge.monitoring.controller.ServiceParameters;
import cipm.consistency.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;

public abstract class SubInstrumentationHelper {
	protected JavaParser javaParserUtil = new JavaParser();

	// get necessary classes and types
	protected ClassOrInterfaceType serviceParametersType = javaParserUtil
			.parseClassOrInterfaceType(ServiceParameters.class.getSimpleName()).getResult().get();
	protected ClassOrInterfaceType atomicIntegerType = javaParserUtil
			.parseClassOrInterfaceType(AtomicInteger.class.getSimpleName()).getResult().get();

	protected NameExpr threadMonitoringControllerReference = new NameExpr(
			ApplicationProjectInstrumenterNamespace.THREAD_MONITORING_CONTROLLER_VARIABLE);
	protected NameExpr serviceParametersReference = new NameExpr(
			ApplicationProjectInstrumenterNamespace.SERVICE_PARAMETERS_VARIABLE);

	protected SubInstrumentationHelper() {
	}

	protected void addBefore(Statement statement, Statement before) {
		if (statement.getParentNode().isPresent()) {
			BlockStmt block = statement.getParentNode().map(parent -> (BlockStmt) parent).get();
			block.getStatements().addBefore(before, statement);
		}
	}

	protected void addAfter(Statement statement, Statement after) {
		if (statement.getParentNode().isPresent()) {
			BlockStmt block = statement.getParentNode().map(parent -> (BlockStmt) parent).get();
			block.getStatements().addAfter(after, statement);
		}
	}

}
