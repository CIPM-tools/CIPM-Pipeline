package dmodel.pipeline.core.facade;

import java.util.Set;

import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint;

public interface IInstrumentationModelQueryFacade {

	Set<ServiceInstrumentationPoint> getFineGrainedInstrumentedServices();

	Set<String> getInstrumentedActionIds();

}
