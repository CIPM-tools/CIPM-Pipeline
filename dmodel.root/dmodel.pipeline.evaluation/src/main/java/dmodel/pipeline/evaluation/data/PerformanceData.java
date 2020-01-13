package dmodel.pipeline.evaluation.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PerformanceData {

	private List<ExecutionData> executionData;

	public PerformanceData() {
		this.executionData = new ArrayList<ExecutionData>();
	}

}
