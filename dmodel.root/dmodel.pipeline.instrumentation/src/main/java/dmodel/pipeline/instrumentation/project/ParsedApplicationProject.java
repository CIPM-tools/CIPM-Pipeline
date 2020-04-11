package dmodel.pipeline.instrumentation.project;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

public class ParsedApplicationProject {
	private List<Pair<SourceRoot, String>> roots;

	@Getter
	@Setter
	private ApplicationProject source;

	public ParsedApplicationProject(ApplicationProject source) {
		this.source = source;
		this.roots = Lists.newArrayList();

		File basePath = new File(source.getRootPath());
		for (String sourceFolder : source.getSourceFolders()) {
			File srcFolder = new File(basePath, sourceFolder);
			roots.add(Pair.of(new SourceRoot(srcFolder.toPath()), sourceFolder));
		}
	}

	public CompilationUnit getCompilationUnit(String pckg, String clazz) {
		List<SourceRoot> possibleRoots = roots.stream().filter(root -> {
			try {
				root.getLeft().parse(pckg, clazz);
				return true;
			} catch (ParseProblemException e) {
				return false;
			}
		}).map(root -> root.getLeft()).collect(Collectors.toList());

		if (possibleRoots.size() == 1) {
			return possibleRoots.get(0).parse(pckg, clazz);
		} else if (possibleRoots.size() > 1) {
			throw new IllegalStateException("Ambiguous Java class definitions cannot be resolved.");
		} else {
			return null;
		}
	}

	public void saveSources(File outputFolder) {
		roots.forEach(root -> {
			File rootOutputFolder = new File(outputFolder, root.getRight());

			root.getLeft().saveAll(rootOutputFolder.toPath(), Charset.forName("UTF-8"));
		});

	}

	public List<SourceRoot> getRoots() {
		return roots.stream().map(p -> p.getLeft()).collect(Collectors.toList());
	}

}
