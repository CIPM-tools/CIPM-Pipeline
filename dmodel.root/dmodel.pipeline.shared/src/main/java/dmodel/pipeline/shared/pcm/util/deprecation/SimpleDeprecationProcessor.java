package dmodel.pipeline.shared.pcm.util.deprecation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.uka.ipd.sdq.identifier.Identifier;

public class SimpleDeprecationProcessor implements IDeprecationProcessor {
	private int consecutives;

	private Map<String, Integer> absences;
	private Set<String> iteration;

	public SimpleDeprecationProcessor(int maxConsecutiveAbsences) {
		this.consecutives = maxConsecutiveAbsences;
		this.absences = new HashMap<>();
		this.iteration = new HashSet<>();
	}

	@Override
	public boolean shouldDelete(Identifier ctx) {
		this.iteration.add(ctx.getId());
		if (absences.containsKey(ctx.getId())) {
			absences.put(ctx.getId(), absences.get(ctx.getId()) + 1);
		} else {
			absences.put(ctx.getId(), 1);
		}
		return absences.get(ctx.getId()) >= consecutives;
	}

	@Override
	public void iterationFinished() {
		Set<String> removeKeys = absences.keySet().stream().filter(ab -> {
			return !this.iteration.contains(ab);
		}).collect(Collectors.toSet());

		removeKeys.forEach(k -> {
			this.absences.remove(k);
		});

		this.iteration.clear();
	}

	@Override
	public boolean isCurrentlyDeprecated(Identifier ctx) {
		return this.absences.keySet().contains(ctx.getId());
	}

}
