package cipm.consistency.designtime.instrumentation.tuid;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;

import cipm.consistency.designtime.instrumentation.project.ParsedApplicationProject;

public interface IJavaParserTuidGeneratorAndResolver {

	public String generateId(Statement stmt);

	public String generateId(ClassOrInterfaceDeclaration type);

	public String generateId(MethodDeclaration method);

	public String generateId(CompilationUnit unit);

	public MethodDeclaration resolveMethod(ParsedApplicationProject project, String methodId);

	public ClassOrInterfaceDeclaration resolveType(ParsedApplicationProject project, String typeId);

	public Statement resolveStatement(ParsedApplicationProject project, String statement);

}
