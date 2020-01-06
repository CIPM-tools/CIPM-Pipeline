package dmodel.pipeline.records.instrument.spoon.instrument;

import InstrumentationMetamodel.ServiceInstrumentationPoint;
import spoon.Launcher;

public interface ISpoonInstrumenter<T> {

	public void instrument(Launcher parent, T target, ServiceInstrumentationPoint probe);

}
