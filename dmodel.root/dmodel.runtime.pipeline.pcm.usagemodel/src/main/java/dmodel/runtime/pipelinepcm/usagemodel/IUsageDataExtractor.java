package dmodel.runtime.pipelinepcm.usagemodel;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import dmodel.base.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.base.core.facade.pcm.ISystemQueryFacade;
import dmodel.base.shared.structure.Tree;
import dmodel.designtime.monitoring.records.ServiceCallRecord;

public interface IUsageDataExtractor {

	public List<UsageScenario> extract(List<Tree<ServiceCallRecord>> callSequences, IRepositoryQueryFacade repository,
			ISystemQueryFacade system);

}
