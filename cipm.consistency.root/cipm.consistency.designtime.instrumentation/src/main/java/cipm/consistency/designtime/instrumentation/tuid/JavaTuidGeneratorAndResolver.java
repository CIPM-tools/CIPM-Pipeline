package cipm.consistency.designtime.instrumentation.tuid;

import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;

import cipm.consistency.designtime.instrumentation.project.ParsedApplicationProject;

public class JavaTuidGeneratorAndResolver implements IJavaParserTuidGeneratorAndResolver {
	private static final String INFORMATION_DELEMITER = "#";
	private static final String JAVA_FILE_SUFFIX = ".java";

	@Override
	public String generateId(MethodDeclaration method) {
		String methodDeclarationHex = DigestUtils.sha1Hex(method.getDeclarationAsString());
		if (method.getParentNode().isPresent() && method.getParentNode().get() instanceof ClassOrInterfaceDeclaration) {
			return generateId((ClassOrInterfaceDeclaration) method.getParentNode().get()) + INFORMATION_DELEMITER
					+ methodDeclarationHex;
		}

		return null;
	}

	@Override
	public MethodDeclaration resolveMethod(ParsedApplicationProject project, String methodId) {
		String[] splitted = methodId.split(INFORMATION_DELEMITER);
		String[] classType = new String[splitted.length - 1];
		System.arraycopy(splitted, 0, classType, 0, splitted.length - 1);

		ClassOrInterfaceDeclaration decl = resolveType(project, String.join(INFORMATION_DELEMITER, classType));
		Optional<MethodDeclaration> target = decl.getMethods().stream().filter(
				meth -> DigestUtils.sha1Hex(meth.getDeclarationAsString()).equals(splitted[splitted.length - 1]))
				.findFirst();

		if (target.isPresent()) {
			return target.get();
		}

		return null;
	}

	@Override
	public String generateId(ClassOrInterfaceDeclaration type) {
		String id = type.getNameAsString();
		if (type.getParentNode().isPresent()) {
			Node parentNode = type.getParentNode().get();
			if (parentNode instanceof ClassOrInterfaceDeclaration) {
				return generateId((ClassOrInterfaceDeclaration) parentNode) + INFORMATION_DELEMITER + id;
			} else if (parentNode instanceof CompilationUnit) {
				return generateId((CompilationUnit) parentNode) + INFORMATION_DELEMITER + id;
			}
		}

		return id;
	}

	@Override
	public ClassOrInterfaceDeclaration resolveType(ParsedApplicationProject project, String typeId) {
		String[] splitted = typeId.split(INFORMATION_DELEMITER);
		if (splitted.length >= 2) {
			CompilationUnit unit = project.getCompilationUnit(splitted[0], splitted[1] + JAVA_FILE_SUFFIX);
			Optional<ClassOrInterfaceDeclaration> base = unit.getClassByName(splitted[1]);
			if (base.isPresent()) {
				for (int i = 2; i < splitted.length; i++) {
					List<ClassOrInterfaceDeclaration> results = base.get().findAll(ClassOrInterfaceDeclaration.class);

					int iCopy = i;
					Optional<ClassOrInterfaceDeclaration> decl = results.stream()
							.filter(c -> c.getNameAsString().equals(splitted[iCopy])).findFirst();

					if (decl.isPresent()) {
						base = decl;
					} else {
						return null;
					}
				}

				return base.get();
			}
		}

		return null;
	}

	@Override
	public String generateId(CompilationUnit unit) {
		return unit.getPackageDeclaration().get().getNameAsString();
	}

	@Override
	public String generateId(Statement stmt) {
		MethodDeclaration parent = getMethodParent(stmt);
		if (parent != null) {
			String methodId = generateId(parent);
			if (stmt.getRange().isPresent()) {
				Range range = stmt.getRange().get();
				String rangeEncoded = range.begin.line + "," + range.begin.column + ";" + range.end.line + ","
						+ range.end.column;

				return methodId + INFORMATION_DELEMITER + rangeEncoded;
			}
		}

		return null;
	}

	@Override
	public Statement resolveStatement(ParsedApplicationProject project, String statement) {
		String[] splitted = statement.split(INFORMATION_DELEMITER);
		String[] methodType = new String[splitted.length - 1];
		System.arraycopy(splitted, 0, methodType, 0, splitted.length - 1);

		MethodDeclaration decl = resolveMethod(project, String.join(INFORMATION_DELEMITER, methodType));

		String[] rangeStartStopSplit = splitted[splitted.length - 1].split(";");
		if (rangeStartStopSplit.length == 2) {
			String[] lineColSplit = rangeStartStopSplit[0].split("\\,");
			String[] lineColSplit2 = rangeStartStopSplit[1].split("\\,");

			if (lineColSplit.length == 2 && lineColSplit2.length == 2) {
				Position start = Position.pos(Integer.parseInt(lineColSplit[0]), Integer.parseInt(lineColSplit[1]));
				Position end = Position.pos(Integer.parseInt(lineColSplit2[0]), Integer.parseInt(lineColSplit2[1]));

				return decl.findAll(Statement.class).stream().filter(stmt -> {
					if (stmt.getRange().isPresent()) {
						return stmt.getRange().get().begin.equals(start) && stmt.getRange().get().end.equals(end);
					}
					return false;
				}).findFirst().orElse(null);
			}
		}

		return null;
	}

	private MethodDeclaration getMethodParent(Statement stmt) {
		Optional<Node> parent = stmt.getParentNode();
		while (parent.isPresent() && !(parent.get() instanceof MethodDeclaration)) {
			parent = parent.get().getParentNode();
		}

		if (parent.isPresent()) {
			return (MethodDeclaration) parent.get();
		}
		return null;
	}

}
