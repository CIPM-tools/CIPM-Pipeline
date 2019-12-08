package dmodel.pipeline.rt.validation.eval;

import org.pcm.headless.shared.data.results.InMemoryResultRepository;

public interface IAnalysisDataProcessor extends IValidationDataGenerator {

	public void processAnalysisData(InMemoryResultRepository results);

}
