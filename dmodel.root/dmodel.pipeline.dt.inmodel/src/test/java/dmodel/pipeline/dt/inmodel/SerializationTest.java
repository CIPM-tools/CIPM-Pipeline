package dmodel.pipeline.dt.inmodel;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.InstrumentationModelFactory;
import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint;

public class SerializationTest {

	public static void main(String[] args) {
		InstrumentationModel model = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();

		ServiceInstrumentationPoint point = InstrumentationModelFactory.eINSTANCE.createServiceInstrumentationPoint();
		point.setActive(true);

	}

}
