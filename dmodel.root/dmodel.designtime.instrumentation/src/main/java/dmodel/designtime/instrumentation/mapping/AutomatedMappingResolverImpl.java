package dmodel.designtime.instrumentation.mapping;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.google.common.collect.Maps;

import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.designtime.instrumentation.mapping.comment.AbstractMappingComment;
import dmodel.designtime.instrumentation.mapping.comment.ExternalCallMappingComment;
import dmodel.designtime.instrumentation.mapping.comment.InternalActionMappingComment;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;
import dmodel.designtime.instrumentation.tuid.JavaTuidGeneratorAndResolver;
import dmodel.designtime.monitoring.util.ManualMapping;

/**
 * Component that automatically resolves mappings between source code elements
 * and architecture model elements. It parses comments within the code to
 * retrieve the mappings.
 * 
 * @author David Monschein
 *
 */
@Service
public class AutomatedMappingResolverImpl implements IAutomatedMappingResolver {
	private JavaTuidGeneratorAndResolver tuidGenerator = new JavaTuidGeneratorAndResolver();
	private MappingCommentParser commentParser = new MappingCommentParser();

	private Map<String, Comment> counterPartMapping;

	/**
	 * Creates a new instance of the automated mapping resolver component.
	 */
	public AutomatedMappingResolverImpl() {
		this.counterPartMapping = Maps.newHashMap();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resolveMappings(ParsedApplicationProject project, IJavaPCMCorrespondenceModel cpm) {
		counterPartMapping.clear();
		if (project.getSource() == null) {
			return;
		}
		cpm.clear();
		project.reparse();

		// get all files for all roots
		File rootPath = new File(project.getSource().getRootPath());
		for (String sourceFolder : project.getSource().getSourceFolders()) {
			File sourceBasePath = new File(rootPath, sourceFolder);
			Collection<File> javaFiles = FileUtils.listFiles(sourceBasePath, new String[] { "java" }, true);

			for (File javaFile : javaFiles) {
				URI relativePath = sourceBasePath.toURI().relativize(javaFile.toURI());
				String[] splittedPath = relativePath.getPath().split("\\/");
				String[] pckgPath = new String[splittedPath.length - 1];
				System.arraycopy(splittedPath, 0, pckgPath, 0, splittedPath.length - 1);

				String packageName = String.join(".", pckgPath);
				String fileName = splittedPath[splittedPath.length - 1];

				inspect(project.getCompilationUnit(packageName, fileName), cpm);
			}
		}
	}

	private void inspect(CompilationUnit cp, IJavaPCMCorrespondenceModel cpm) {
		if (cp != null) {
			cp.findAll(ClassOrInterfaceDeclaration.class).forEach(coid -> inspect(coid, cpm));
		}
	}

	private void inspect(ClassOrInterfaceDeclaration coid, IJavaPCMCorrespondenceModel cpm) {
		coid.getMethods().forEach(meth -> inspect(meth, cpm));
	}

	private void inspect(MethodDeclaration meth, IJavaPCMCorrespondenceModel cpm) {
		Optional<AnnotationExpr> mappingAnnotation = meth.getAnnotationByClass(ManualMapping.class);
		if (mappingAnnotation.isPresent()) {
			SingleMemberAnnotationExpr expr = mappingAnnotation.get().asSingleMemberAnnotationExpr();
			Expression memberValue = expr.getMemberValue();
			String correspondingSeffId = memberValue.asStringLiteralExpr().getValue();

			cpm.addSeffCorrespondence(tuidGenerator.generateId(meth), correspondingSeffId);
		}

		if (meth.getBody().isPresent()) {
			inspect(meth.getBody().get(), cpm);
		}
	}

	private void inspect(BlockStmt body, IJavaPCMCorrespondenceModel cpm) {
		for (Comment comment : body.getAllContainedComments()) {
			Optional<AbstractMappingComment> parsedComment = commentParser.parseComment(comment.getContent());

			if (parsedComment.isPresent()) {
				processParsedComment(parsedComment.get(), comment, cpm);
			}
		}
	}

	private void processParsedComment(AbstractMappingComment parsedComment, Comment comment,
			IJavaPCMCorrespondenceModel cpm) {
		if (parsedComment.isInternalActionMapping()) {
			processInternalActionComment(parsedComment.asInternalActionMappingComment(), comment, cpm);
		} else if (parsedComment.isBranchMapping()) {
			Node branchNode = getContainingNode(comment);
			if (branchNode instanceof Statement) {
				String generatedBranchId = tuidGenerator.generateId((Statement) branchNode);
				cpm.addBranchCorrespondence(generatedBranchId,
						parsedComment.asBranchMappingComment().getBranchTransitionId());
			}
		} else if (parsedComment.isLoopMapping()) {
			if (comment.getCommentedNode().isPresent() && comment.getCommentedNode().get() instanceof Statement) {
				String generatedLoopId = tuidGenerator.generateId((Statement) comment.getCommentedNode().get());
				cpm.addLoopCorrespondence(generatedLoopId, parsedComment.asLoopMappingComment().getLoopId());
			}
		} else if (parsedComment.isExternalCallMapping()) {
			processExternalCallComment(parsedComment.asExternalCallMappingComment(), comment, cpm);
		}
	}

	private void processExternalCallComment(ExternalCallMappingComment parsedComment, Comment comment,
			IJavaPCMCorrespondenceModel cpm) {
		Statement externalMethodCall = getSubsequentStatement(comment);
		if (externalMethodCall != null) {
			cpm.addExternalCallCorrespondence(tuidGenerator.generateId(externalMethodCall),
					parsedComment.getExternalCallId());
		}
	}

	private void processInternalActionComment(InternalActionMappingComment parsedComment, Comment comment,
			IJavaPCMCorrespondenceModel cpm) {
		if (!counterPartMapping.containsKey(parsedComment.getInternalActionId())) {
			counterPartMapping.put(parsedComment.getInternalActionId(), comment);
			return;
		}

		Comment counterPart = counterPartMapping.get(parsedComment.getInternalActionId());
		counterPartMapping.remove(parsedComment.getInternalActionId());

		if (comment.getRange().isPresent() && counterPart.getRange().isPresent()) {
			Comment startNode = parsedComment.isStart() ? comment : counterPart;
			Comment endNode = parsedComment.isStart() ? counterPart : comment;

			Statement startStatement = getSubsequentStatement(startNode);
			Statement endStatement = getPrecedingStatement(endNode);

			if (startStatement != null && endStatement != null) {
				String startExprId = tuidGenerator.generateId(startStatement);
				String endExprId = tuidGenerator.generateId(endStatement);

				cpm.addInternalActionCorrespondence(startExprId, endExprId, parsedComment.getInternalActionId());
			}
		}
	}

	private Statement getPrecedingStatement(Comment node) {
		Node parentNode = getContainingNode(node);
		Range ownRange = node.getRange().get();

		int minDistanceLine = Integer.MAX_VALUE;
		int minDistanceCol = Integer.MAX_VALUE;
		Statement nearestElement = null;

		for (Node child : parentNode.getChildNodes()) {
			if (child instanceof Statement && child.getRange().isPresent()) {
				Range childRange = child.getRange().get();
				if (childRange.isBefore(ownRange.begin)) {
					int lineDistance = ownRange.begin.line - childRange.end.line;
					int colDistance = ownRange.begin.column - childRange.end.column;

					if (lineDistance < minDistanceLine
							|| (lineDistance == minDistanceLine && colDistance < minDistanceCol)) {
						minDistanceLine = lineDistance;
						minDistanceCol = colDistance;

						nearestElement = (Statement) child;
					}
				}
			}
		}

		if (nearestElement == null) {
			if (parentNode instanceof Statement) {
				return (Statement) parentNode;
			}
		} else {
			return nearestElement;
		}

		return null;
	}

	private Statement getSubsequentStatement(Comment node) {
		if (node.getCommentedNode().isPresent() && node.getCommentedNode().get() instanceof Statement) {
			return (Statement) node.getCommentedNode().get();
		}
		return null;
	}

	private Node getContainingNode(Comment node) {
		if (node.getParentNode().isPresent()) {
			return node.getParentNode().get();
		}
		return node.getCommentedNode().get().getParentNode().get();
	}

}
