package dmodel.pipeline.rt.pcm.repository.data;

import dmodel.pipeline.monitoring.util.ServiceParametersWrapper;

public class ParametersWithClass<T> {
	private final ServiceParametersWrapper parameters;

	private final T classValue;

	private double weight;

	public ParametersWithClass(ServiceParametersWrapper parameters, T classValue) {
		this(parameters, classValue, 1.0);
	}

	public ParametersWithClass(ServiceParametersWrapper parameters, T classValue, double weight) {
		this.parameters = parameters;
		this.classValue = classValue;
		this.weight = weight;
	}

	public ServiceParametersWrapper getParameters() {
		return parameters;
	}

	public T getClassValue() {
		return classValue;
	}

	public double getWeight() {
		return weight;
	}

}
