package dmodel.runtime.pipeline.pcm.repository.data;

import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

public class IfCondition {

	private PCMRandomVariable var;

	public IfCondition(String val) {
		this.var = CoreFactory.eINSTANCE.createPCMRandomVariable();
		var.setSpecification(val);
	}

	public void and(IfCondition other) {
		PCMRandomVariable combined = CoreFactory.eINSTANCE.createPCMRandomVariable();
		combined.setSpecification(var.getSpecification() + " && " + other.var.getSpecification());
		this.var = combined;
	}

	public PCMRandomVariable getStoex() {
		return this.var;
	}

	@Override
	public int hashCode() {
		return var.getSpecification().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof IfCondition) {
			return this.var.getSpecification() != null
					&& this.var.getSpecification().equals(((IfCondition) other).var.getSpecification());
		}
		return false;
	}

}
