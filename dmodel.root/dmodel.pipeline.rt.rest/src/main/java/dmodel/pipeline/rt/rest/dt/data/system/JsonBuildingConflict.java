package dmodel.pipeline.rt.rest.dt.data.system;

public class JsonBuildingConflict {

	private long id;
	private String type;

	private String[] possibleIds;
	private String[] targetIds;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getPossibleIds() {
		return possibleIds;
	}

	public void setPossibleIds(String[] possibleIds) {
		this.possibleIds = possibleIds;
	}

	public String[] getTargetIds() {
		return targetIds;
	}

	public void setTargetIds(String[] targetIds) {
		this.targetIds = targetIds;
	}

	public void setTargetId(String targetId) {
		this.targetIds = new String[] { targetId };
	}

}
