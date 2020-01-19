package dmodel.pipeline.records.instrument.spoon.instrument.impl;

import java.util.List;

import InstrumentationMetamodel.ActionInstrumentationPoint;
import InstrumentationMetamodel.InstrumentationPoint;
import InstrumentationMetamodel.InstrumentationType;
import dmodel.pipeline.records.instrument.spoon.instrument.ISpoonInstrumenter;
import spoon.Launcher;
import spoon.reflect.code.CtStatement;
import spoon.reflect.factory.Factory;

public class SpoonInternalInstrumenter implements ISpoonInstrumenter<List<CtStatement>> {

	@Override
	public void instrument(Launcher parent, List<CtStatement> target, InstrumentationPoint probe) {
		if (!(probe instanceof ActionInstrumentationPoint)) {
			return;
		}
		ActionInstrumentationPoint point = (ActionInstrumentationPoint) probe;
		if (point.getType() != InstrumentationType.INTERNAL) {
			return;
		}

		Factory factory = parent.getFactory();

		if (probe.isActive()) {
			CtStatement firstStatement;
			CtStatement lastStatement;
		}
	}

}
