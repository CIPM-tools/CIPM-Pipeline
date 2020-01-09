package dmodel.pipeline.records.instrument.spoon.instrument.impl;

import InstrumentationMetamodel.InstrumentationPoint;
import dmodel.pipeline.records.instrument.spoon.instrument.ISpoonInstrumenter;
import spoon.Launcher;
import spoon.reflect.code.CtIf;

public class SpoonBranchInstrumenter implements ISpoonInstrumenter<CtIf> {

	@Override
	public void instrument(Launcher parent, CtIf target, InstrumentationPoint probe) {
		// TODO Auto-generated method stub

	}

}
