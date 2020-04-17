package dmodel.pipeline.rt.rest.dt.data.system;

public class JsonSystemConnector {

	private boolean delegation;
	private boolean delegationDirection;

	private String assemblyFrom;
	private String assemblyTo;

	private String role1;
	private String role2;

	private String id;
	private String name;

	public boolean isDelegation() {
		return delegation;
	}

	public void setDelegation(boolean delegation) {
		this.delegation = delegation;
	}

	public String getAssemblyFrom() {
		return assemblyFrom;
	}

	public void setAssemblyFrom(String assemblyFrom) {
		this.assemblyFrom = assemblyFrom;
	}

	public String getAssemblyTo() {
		return assemblyTo;
	}

	public void setAssemblyTo(String assemblyTo) {
		this.assemblyTo = assemblyTo;
	}

	public String getRole1() {
		return role1;
	}

	public void setRole1(String role1) {
		this.role1 = role1;
	}

	public String getRole2() {
		return role2;
	}

	public void setRole2(String role2) {
		this.role2 = role2;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDelegationDirection() {
		return delegationDirection;
	}

	public void setDelegationDirection(boolean delegationDirection) {
		this.delegationDirection = delegationDirection;
	}

}
