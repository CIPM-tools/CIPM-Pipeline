package dmodel.pipeline.rt.pipeline.blackboard;

import dmodel.pipeline.dt.mmmodel.MeasurementModel;
import dmodel.pipeline.dt.mmmodel.MmmodelFactory;

public class RuntimePipelineBlackboard {

	private MeasurementModel measurementModel;

	public RuntimePipelineBlackboard() {
		this.reset();
	}

	public MeasurementModel getMeasurementModel() {
		return measurementModel;
	}

	public void setMeasurementModel(MeasurementModel measurementModel) {
		this.measurementModel = measurementModel;
	}

	public void reset() {
		measurementModel = MmmodelFactory.eINSTANCE.createMeasurementModel();
	}

}
