package dmodel.pipeline.rt.pcm.repository;

import java.util.List;

import org.springframework.stereotype.Service;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.validation.data.ValidationMetric;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.structure.Tree;

@Service
public class RepositoryDerivation {

	public void calibrateRepository(List<Tree<ServiceCallRecord>> callTrees, InMemoryPCM pcm,
			List<ValidationMetric> validation) {
	}

}
