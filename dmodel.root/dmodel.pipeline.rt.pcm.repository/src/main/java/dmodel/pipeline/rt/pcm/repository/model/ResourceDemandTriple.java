package dmodel.pipeline.rt.pcm.repository.model;

import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;

import dmodel.pipeline.monitoring.util.ServiceParametersWrapper;

public class ResourceDemandTriple {
	private ProcessingResourceSpecification resource;
	private ParametricResourceDemand demand;
	private double time; // in ms

	private String actionId;

	private ServiceParametersWrapper parameters;

	public ResourceDemandTriple(String actionId, ProcessingResourceSpecification resource,
			ParametricResourceDemand demand, double time) {
		super();
		this.resource = resource;
		this.demand = demand;
		this.time = time;
		this.setActionId(actionId);
	}

	// TODO currently only works with static process rates (standard)
	public double approximateProcessRate(int steps) {
		PCMRandomVariable var = resource.getProcessingRate_ProcessingResourceSpecification();

		double procRate;
		try {
			procRate = Double.parseDouble(var.getSpecification());
		} catch (NumberFormatException e) {
			procRate = 0;
		}

		return (resource.getMTTF() / (resource.getMTTF() + resource.getMTTR())) * procRate;
	}

	public ProcessingResourceSpecification getResource() {
		return resource;
	}

	public void setResource(ProcessingResourceSpecification resource) {
		this.resource = resource;
	}

	public ParametricResourceDemand getDemand() {
		return demand;
	}

	public void setDemand(ParametricResourceDemand demand) {
		this.demand = demand;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public ServiceParametersWrapper getParameters() {
		return parameters;
	}

	public void setParameters(ServiceParametersWrapper parameters) {
		this.parameters = parameters;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

}
