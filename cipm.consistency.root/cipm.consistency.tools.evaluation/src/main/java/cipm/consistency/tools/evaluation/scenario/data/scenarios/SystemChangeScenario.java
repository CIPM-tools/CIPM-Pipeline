package cipm.consistency.tools.evaluation.scenario.data.scenarios;

import java.util.Set;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.pcm.headless.api.util.PCMUtil;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenario;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioExecutionConfig;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioType;
import cipm.consistency.tools.evaluation.scenario.data.teastore.RecommenderType;
import cipm.consistency.tools.evaluation.scenario.helper.DefaultHttpClient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemChangeScenario extends AdaptionScenario {
	private static final Set<String> RECOMMENDER_TYPE_IDS = Sets.newHashSet("_raxjcDVgEeqPG_FgW3bi6Q",
			"_YkXeIDVgEeqPG_FgW3bi6Q", "_iaElgKpwEeqHXcsU55mirw", "_kgbngDVgEeqPG_FgW3bi6Q", "_ouzFYDVgEeqPG_FgW3bi6Q");
	private static DefaultHttpClient http = new DefaultHttpClient(5000L);

	private RecommenderType newType;

	public SystemChangeScenario() {
		this(null);
	}

	public SystemChangeScenario(RecommenderType type) {
		super(AdaptionScenarioType.SYSTEM_COMPOSITION);
		this.newType = type;
	}

	@Override
	public void execute(AdaptionScenarioExecutionConfig config) {
		String baseUrl = config.getTeastoreRestURL().endsWith("/") ? config.getTeastoreRestURL()
				: config.getTeastoreRestURL() + "/";
		String finalUrl = baseUrl + "recommend/changeRecommender?id=" + String.valueOf(newType.getId());
		http.getRequest(finalUrl, Maps.newHashMap());
	}

	@Override
	public InMemoryPCM generateReferenceModel(InMemoryPCM current) {
		InMemoryPCM copy = current.copyDeep();

		// modify system
		BasicComponent nComponent = PCMUtil.getElementById(copy.getRepository(), BasicComponent.class,
				newType.getComponentId());

		// search belonging assembly
		AssemblyContext currentUsedRecommender = null;
		for (AssemblyContext ctx : copy.getSystem().getAssemblyContexts__ComposedStructure()) {
			if (RECOMMENDER_TYPE_IDS.contains(ctx.getEncapsulatedComponent__AssemblyContext().getId())) {
				currentUsedRecommender = ctx;
			}
		}

		if (currentUsedRecommender != null) {
			currentUsedRecommender.setEncapsulatedComponent__AssemblyContext(nComponent);
		}

		return copy;
	}

}
