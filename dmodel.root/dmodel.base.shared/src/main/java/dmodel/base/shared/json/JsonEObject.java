package dmodel.base.shared.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

import lombok.Data;

@Data
public class JsonEObject {

	private String type;

	private List<JsonEObject> childs;
	private Map<String, String> attributes;

	public static JsonEObject create(EObject clone) {
		JsonEObject ret = new JsonEObject();

		ret.setType(clone.eClass().getName());

		// get fields
		for (EAttribute field : clone.eClass().getEAllAttributes()) {
			ret.attributes.put(field.getName(), String.valueOf(clone.eGet(field)));
		}

		for (EObject child : clone.eContents()) {
			ret.childs.add(JsonEObject.create(child));
		}

		return ret;
	}

	private JsonEObject() {
		this.childs = new ArrayList<>();
		this.attributes = new HashMap<>();
	}

}
