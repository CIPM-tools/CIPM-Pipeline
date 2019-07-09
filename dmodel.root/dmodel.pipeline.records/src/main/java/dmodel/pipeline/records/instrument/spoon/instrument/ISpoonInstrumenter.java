package dmodel.pipeline.records.instrument.spoon.instrument;

import spoon.Launcher;
import tools.vitruv.models.im.InstrumentationPoint;

public interface ISpoonInstrumenter<T> {

	public void instrument(Launcher parent, T target, InstrumentationPoint probe);

}
