package dmodel.pipeline.rt.pcm.finalize;

import dmodel.pipeline.dt.mmmodel.MeasurementModel;
import dmodel.pipeline.models.mapping.PalladioRuntimeMapping;
import dmodel.pipeline.shared.pcm.InMemoryPCM;

public interface IMeasurementModelProcessor {

	public void processMeasurementModel(MeasurementModel mm, InMemoryPCM pcm, PalladioRuntimeMapping mapping);

}
