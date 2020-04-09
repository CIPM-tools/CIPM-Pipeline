package dmodel.pipeline.rt.pcm.usagemodel;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import dmodel.pipeline.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.pipeline.core.facade.pcm.ISystemQueryFacade;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.shared.structure.Tree;

public interface IUsageDataExtractor {

	public List<UsageScenario> extract(List<Tree<ServiceCallRecord>> callSequences, IRepositoryQueryFacade repository,
			ISystemQueryFacade system);

}
