package dmodel.pipeline.shared.pcm;

import java.util.List;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class PCMInstance {
	
	private List<Repository> repositories;
	private System system;
	private UsageModel usage;
	private Allocation allocation;
	private ResourceEnvironment environment;

	public List<Repository> getRepositories() {
		return repositories;
	}

	public void setRepositories(List<Repository> repositories) {
		this.repositories = repositories;
	}

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}

	public UsageModel getUsage() {
		return usage;
	}

	public void setUsage(UsageModel usage) {
		this.usage = usage;
	}

	public Allocation getAllocation() {
		return allocation;
	}

	public void setAllocation(Allocation allocation) {
		this.allocation = allocation;
	}

	public ResourceEnvironment getEnvironment() {
		return environment;
	}

	public void setEnvironment(ResourceEnvironment environment) {
		this.environment = environment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allocation == null) ? 0 : allocation.hashCode());
		result = prime * result + ((environment == null) ? 0 : environment.hashCode());
		result = prime * result + ((repositories == null) ? 0 : repositories.hashCode());
		result = prime * result + ((system == null) ? 0 : system.hashCode());
		result = prime * result + ((usage == null) ? 0 : usage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PCMInstance other = (PCMInstance) obj;
		if (allocation == null) {
			if (other.allocation != null)
				return false;
		} else if (!allocation.equals(other.allocation))
			return false;
		if (environment == null) {
			if (other.environment != null)
				return false;
		} else if (!environment.equals(other.environment))
			return false;
		if (repositories == null) {
			if (other.repositories != null)
				return false;
		} else if (!repositories.equals(other.repositories))
			return false;
		if (system == null) {
			if (other.system != null)
				return false;
		} else if (!system.equals(other.system))
			return false;
		if (usage == null) {
			if (other.usage != null)
				return false;
		} else if (!usage.equals(other.usage))
			return false;
		return true;
	}

}
