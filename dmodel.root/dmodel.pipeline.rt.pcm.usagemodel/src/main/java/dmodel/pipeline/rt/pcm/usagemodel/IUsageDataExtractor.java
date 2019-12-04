package dmodel.pipeline.rt.pcm.usagemodel;

import java.util.List;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.shared.structure.Tree;

public interface IUsageDataExtractor {

	public List<UsageScenario> extract(List<Tree<ServiceCallRecord>> callSequences, Repository repository,
			System system);

}
