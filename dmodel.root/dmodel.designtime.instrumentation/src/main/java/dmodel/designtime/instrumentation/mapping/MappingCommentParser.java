package dmodel.designtime.instrumentation.mapping;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmodel.designtime.instrumentation.mapping.comment.AbstractMappingComment;
import dmodel.designtime.instrumentation.mapping.comment.BranchMappingComment;
import dmodel.designtime.instrumentation.mapping.comment.ExternalCallMappingComment;
import dmodel.designtime.instrumentation.mapping.comment.InternalActionMappingComment;
import dmodel.designtime.instrumentation.mapping.comment.LoopMappingComment;

/**
 * Parser for comments in the source code that describe mappings to architecture
 * model elements.
 * 
 * @author David Monschein
 *
 */
public class MappingCommentParser {
	private static final Pattern ACTION_PATTERN = Pattern.compile("@(.*) ");
	private static final Pattern TARGET_PATTERN = Pattern.compile("(.*)\\{.*?\\}");
	private static final Pattern TARGET_ID_PATTERN = Pattern.compile("\\{(.*?)\\}");

	private static final String KEYWORD_ENTER = "ENTER";
	private static final String KEYWORD_START = "START";
	private static final String KEYWORD_END = "END";
	private static final String KEYWORD_CALL = "CALL";

	private static final String KEYWORD_TYPE_INTERNAL_ACTION = "INTERNAL_ACTION";
	private static final String KEYWORD_TYPE_LOOP = "LOOP";
	private static final String KEYWORD_TYPE_BRANCH = "BRANCH";
	private static final String KEYWORD_TYPE_EXTERNAL_CALL = "EXTERNAL_CALL";

	/**
	 * Parses a specific comment and returns the parsed information about the
	 * mapping (if present).
	 * 
	 * @param content the comment to parse
	 * @return the parsed content or an empty optional if the comment does not
	 *         contain valid mappings
	 */
	public Optional<AbstractMappingComment> parseComment(String content) {
		if (!content.trim().startsWith("@")) {
			return Optional.empty();
		}

		// match action pattern
		Matcher actionPatternMatcher = ACTION_PATTERN.matcher(content.trim());
		if (actionPatternMatcher.find()) {
			String keyword = actionPatternMatcher.group(1);
			String[] spaceSplit = content.trim().split(" ");
			if (spaceSplit.length < 2) {
				return Optional.empty();
			}

			if (keyword.equals(KEYWORD_ENTER)) {
				// branch or loop
				return parseLoopOrBranch(spaceSplit[1]);
			} else if (keyword.equals(KEYWORD_START) || keyword.equals(KEYWORD_END)) {
				return parseInternalAction(spaceSplit[1], keyword.equals(KEYWORD_START));
			} else if (keyword.equals(KEYWORD_CALL)) {
				return parseExternalCallAction(spaceSplit[1]);
			}
		}

		// otherwise return empty
		return Optional.empty();
	}

	private Optional<AbstractMappingComment> parseExternalCallAction(String content) {
		Matcher targetIdMatcher = TARGET_ID_PATTERN.matcher(content);
		Matcher targetTypeMatcher = TARGET_PATTERN.matcher(content);

		if (targetIdMatcher.find() && targetTypeMatcher.find()) {
			if (targetTypeMatcher.group(1).trim().equals(KEYWORD_TYPE_EXTERNAL_CALL)) {
				return Optional.of(new ExternalCallMappingComment(targetIdMatcher.group(1).trim()));
			}
		}

		return null;
	}

	private Optional<AbstractMappingComment> parseLoopOrBranch(String content) {
		Matcher targetIdMatcher = TARGET_ID_PATTERN.matcher(content);
		Matcher targetTypeMatcher = TARGET_PATTERN.matcher(content);

		if (targetIdMatcher.find() && targetTypeMatcher.find()) {
			String typeKeyword = targetTypeMatcher.group(1).trim();
			if (typeKeyword.equals(KEYWORD_TYPE_BRANCH)) {
				return Optional.of(new BranchMappingComment(targetIdMatcher.group(1).trim()));
			} else if (typeKeyword.equals(KEYWORD_TYPE_LOOP)) {
				return Optional.of(new LoopMappingComment(targetIdMatcher.group(1).trim()));
			}
		}

		return Optional.empty();
	}

	private Optional<AbstractMappingComment> parseInternalAction(String content, boolean isStart) {
		Matcher targetIdMatcher = TARGET_ID_PATTERN.matcher(content);
		Matcher targetTypeMatcher = TARGET_PATTERN.matcher(content);

		if (targetIdMatcher.find() && targetTypeMatcher.find()) {
			if (targetTypeMatcher.group(1).trim().equals(KEYWORD_TYPE_INTERNAL_ACTION)) {
				return Optional.of(new InternalActionMappingComment(isStart, targetIdMatcher.group(1).trim()));
			}
		}

		return Optional.empty();
	}

}
