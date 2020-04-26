package dmodel.app.rest.dt.data.system;

import java.util.Map;

import lombok.Data;

@Data
public class JsonConflictSolution {

	private long id;
	private String solution;

	private Map<String, String> nameMapping;

}
