package dmodel.pipeline.records.instrument.spoon.instrument;

import InstrumentationMetamodel.InstrumentationPoint;
import spoon.Launcher;

public interface ISpoonInstrumenter<T> {

	public void instrument(Launcher parent, T target, InstrumentationPoint probe);

}
