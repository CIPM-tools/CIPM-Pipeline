package cipm.consistency.tools.evaluation.scalability.generator;

import org.apache.commons.lang3.tuple.Pair;

public interface StartStopTimeGenerator {
	Pair<Long, Long> generateNextInterval();
}
