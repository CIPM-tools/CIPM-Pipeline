package dmodel.pipeline.records.instrument.spoon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import de.uka.ipd.sdq.identifier.Identifier;
import dmodel.pipeline.models.mapping.RepositoryMapping;
import dmodel.pipeline.shared.pcm.PCMUtils;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtPathStringBuilder;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.Correspondences;
import tools.vitruv.framework.tuid.Tuid;
import tools.vitruv.framework.util.VitruviusConstants;

public class SpoonCorrespondenceUtil {

	private static final String PATTERN_PALLADIO = "http://palladiosimulator.org/PalladioComponentModel/.*";
	private static final String PATTERN_JAVA = "http://www.emftext.org/java";

	public static SpoonCorrespondence transformCorrespondence(Correspondences corr, CtModel model, Repository repo) {
		SpoonCorrespondence correspondence = new SpoonCorrespondence(model, repo);

		for (Correspondence co : corr.getCorrespondences()) {
			// resolve the from
			List<Tuid> froms = co.getATuids();
			List<Tuid> tos = co.getBTuids();

			List<Tuid> pcms = null;
			List<Tuid> javas = null;

			if (froms.size() > 0) {
				// which domain?
				String[] split = splitTuid(froms.get(0).toString());
				boolean isPcm = split[0].matches(PATTERN_PALLADIO);
				boolean isJava = split[0].matches(PATTERN_JAVA);

				if (isPcm)
					pcms = froms;
				else if (isJava)
					javas = froms;
			}

			if (tos.size() > 0) {
				// which domain?
				String[] split = splitTuid(tos.get(0).toString());
				boolean isPcm = split[0].matches(PATTERN_PALLADIO);
				boolean isJava = split[0].matches(PATTERN_JAVA);

				if (isPcm)
					pcms = tos;
				else if (isJava)
					javas = tos;
			}

			if (pcms != null && javas != null) {
				buildCorrespondence(pcms, javas, correspondence, model, repo);
			}
		}

		return correspondence;
	}

	private static void buildCorrespondence(List<Tuid> pcm, List<Tuid> java, SpoonCorrespondence corr, CtModel model,
			Repository repo) {

		List<Identifier> pcmElements = new ArrayList<>();

		for (Tuid pcmTuid : pcm) {
			String[] tuidPre = splitTuid(pcmTuid.toString());
			String[] idSplit = tuidPre[tuidPre.length - 1].split("=");
			if (idSplit.length > 1) {
				String elementId = idSplit[idSplit.length - 1];
				Optional<Identifier> pcmElement = PCMUtils.getElementById(repo, elementId);
				if (pcmElement.isPresent()) {
					pcmElements.add(pcmElement.get());
				}
			}
		}

	}

	public static SpoonCorrespondence buildFromMapping(RepositoryMapping mapping, CtModel spoon,
			Repository repository) {
		SpoonCorrespondence output = new SpoonCorrespondence(spoon, repository);

		mapping.getSpoonToPCM().forEach(pair -> {
			CtPath path = new CtPathStringBuilder().fromString(pair.getSpoonPath());
			List<CtElement> ctEls = path.evaluateOn(spoon.getRootPackage());

			Optional<Identifier> corrElement = PCMUtils.getElementById(repository, pair.getPcmElementId());
			if (corrElement.isPresent()) {
				Identifier el = corrElement.get();
				if (el instanceof ResourceDemandingSEFF && ctEls.size() == 1 && ctEls.get(0) instanceof CtMethod<?>) {
					output.linkService((CtMethod<?>) ctEls.get(0), (ResourceDemandingSEFF) el);
				} else if (el instanceof AbstractAction) {
					List<CtStatement> extractedStatements = ctEls.stream().filter(st -> st instanceof CtStatement)
							.map(st -> (CtStatement) st).collect(Collectors.toList());
					output.linkAbstractAction(extractedStatements, (AbstractAction) el);
				}
			}
		});

		return output;
	}

	private static String[] splitTuid(String tuid) {
		return tuid.split(VitruviusConstants.getTuidSegmentSeperator());
	}

}
