package dmodel.pipeline.rt.validation.data;

import java.util.List;

import com.google.common.collect.Lists;

public abstract class DistributionMetric extends ValidationMetricValue {

	protected List<Double> valuesA;
	protected List<Double> valuesB;

	public DistributionMetric() {
		this.valuesA = Lists.newArrayList();
		this.valuesB = Lists.newArrayList();
	}

	public void addValueA(double val) {
		this.valuesA.add(val);
	}

	public void addValueB(double val) {
		this.valuesB.add(val);
	}

}
