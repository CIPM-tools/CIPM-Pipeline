package dmodel.app.rest.dt.data.system;

import java.util.Map;

public class JsonConflictSolution {

	private long id;
	private String solution;

	private Map<String, String> nameMapping;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public Map<String, String> getNameMapping() {
		return nameMapping;
	}

	public void setNameMapping(Map<String, String> nameMapping) {
		this.nameMapping = nameMapping;
	}

}
