package dmodel.pipeline.rt.pcm.usagemodel;

import java.util.List;

import dmodel.pipeline.dt.mmmodel.UsageData;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.shared.structure.Tree;

public interface IUsageDataExtractor {

	public UsageData extract(List<Tree<ServiceCallRecord>> callSequences);

}
