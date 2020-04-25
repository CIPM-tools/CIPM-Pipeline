package dmodel.app.rest.dt.data.scg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonServiceCallGraphEdge {
	private String from;
	private String to;
	private String extName;
}
