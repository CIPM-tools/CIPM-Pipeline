package cipm.consistency.tools.evaluation.scalability.generator;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class AbstractMonitoringDataGenerator {
	private static final Random RANDOM_GENERATOR = new Random();

	private List<AbstractMonitoringDataGenerator> childs;
	private Optional<AbstractMonitoringDataGenerator> parent;

	private int min;
	private int max;

	public void addChild(AbstractMonitoringDataGenerator child) {
		if (childs == null) {
			this.childs = Lists.newArrayList();
		}

		this.childs.add(child);
		child.parent = Optional.of(this);
	}

	public List<PCMContextRecord> generateData(String sessionId) {
		return this.generateData(sessionId, Optional.empty());
	}

	public List<PCMContextRecord> generateData(String sessionId, Optional<String> parentId) {
		List<PCMContextRecord> output = Lists.newArrayList();
		Optional<String> currentParent = parentId;
		for (int i = 0; i < RANDOM_GENERATOR.nextInt(max - min + 1) + min; i++) {
			Pair<PCMContextRecord, Optional<String>> nRecord = this.generateRecord(sessionId, currentParent);
			currentParent = nRecord.getRight();
			output.add(nRecord.getLeft());
			if (childs != null) {
				for (AbstractMonitoringDataGenerator child : childs) {
					output.addAll(child.generateData(sessionId, currentParent));
				}
			}
		}
		return output;
	}

	protected abstract Pair<PCMContextRecord, Optional<String>> generateRecord(String sessionId,
			Optional<String> parentId);

}
