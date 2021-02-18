package cipm.consistency.app.rest.dt.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class JsonRepositoryComponent {

	private String id;
	private String entityName;

	private List<String> providedInterfaces;
	private List<String> requiredInterfaces;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public List<String> getProvidedInterfaces() {
		return providedInterfaces;
	}

	public void setProvidedInterfaces(List<String> providedInterfaces) {
		this.providedInterfaces = providedInterfaces;
	}

	public List<String> getRequiredInterfaces() {
		return requiredInterfaces;
	}

	public void setRequiredInterfaces(List<String> requiredInterfaces) {
		this.requiredInterfaces = requiredInterfaces;
	}

}
