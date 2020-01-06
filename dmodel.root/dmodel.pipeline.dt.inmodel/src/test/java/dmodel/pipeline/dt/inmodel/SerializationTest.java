package dmodel.pipeline.dt.inmodel;

import InstrumentationMetamodel.InstrumentationModel;
import InstrumentationMetamodel.InstrumentationModelFactory;
import InstrumentationMetamodel.ServiceInstrumentationPoint;

public class SerializationTest {

	public static void main(String[] args) {
		InstrumentationModel model = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();

		ServiceInstrumentationPoint point = InstrumentationModelFactory.eINSTANCE.createServiceInstrumentationPoint();
		point.setActive(true);

	}

}
