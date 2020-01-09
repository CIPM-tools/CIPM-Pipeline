package dmodel.pipeline.records.instrument.spoon.instrument.impl;

import java.util.List;

import InstrumentationMetamodel.InstrumentationPoint;
import dmodel.pipeline.records.instrument.spoon.instrument.ISpoonInstrumenter;
import spoon.Launcher;
import spoon.reflect.code.CtStatement;

public class SpoonInternalInstrumenter implements ISpoonInstrumenter<List<CtStatement>> {

	@Override
	public void instrument(Launcher parent, List<CtStatement> target, InstrumentationPoint probe) {
		// TODO Auto-generated method stub

	}

}
