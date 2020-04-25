package dmodel.base.models.inmodel;

import dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModel;
import dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelFactory;
import dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint;

public class SerializationTest {

	public static void main(String[] args) {
		InstrumentationModel model = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();

		ServiceInstrumentationPoint point = InstrumentationModelFactory.eINSTANCE.createServiceInstrumentationPoint();
		point.setActive(true);

	}

}
