package cipm.consistency.runtime.pipeline.pcm.usagemodel;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import cipm.consistency.base.core.facade.pcm.IRepositoryQueryFacade;
import cipm.consistency.base.core.facade.pcm.ISystemQueryFacade;
import cipm.consistency.base.shared.structure.Tree;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;

public interface IUsageDataExtractor {

	public List<UsageScenario> extract(List<Tree<ServiceCallRecord>> callSequences, IRepositoryQueryFacade repository,
			ISystemQueryFacade system);

}
