package dmodel.designtime.instrumentation.project;

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
import lombok.extern.java.Log;

@Log
public class ParsedApplicationProject {
	private List<Pair<SourceRoot, String>> roots;

	@Getter
	@Setter
	private ApplicationProject source;

	public ParsedApplicationProject(ApplicationProject source) {
		this.source = source;
		this.roots = Lists.newArrayList();

		this.reparse();
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

		log.fine("Search for " + pckg + "." + clazz);
		possibleRoots.forEach(r -> log.fine("Possible root: " + r.getRoot()));

		if (possibleRoots.size() == 1) {
			return possibleRoots.get(0).parse(pckg, clazz);
		} else if (possibleRoots.size() > 1) {
			log.fine("Ambiguous Java class definitions for '" + pckg + "." + clazz + "'.");
			return possibleRoots.get(0).parse(pckg, clazz);
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

	public void reparse() {
		roots.clear();

		File basePath = new File(source.getRootPath());
		for (String sourceFolder : source.getSourceFolders()) {
			File srcFolder = new File(basePath, sourceFolder);
			roots.add(Pair.of(new SourceRoot(srcFolder.toPath()), sourceFolder));
		}
	}

}
