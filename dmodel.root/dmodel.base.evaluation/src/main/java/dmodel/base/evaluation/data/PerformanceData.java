package dmodel.base.evaluation.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Stores performance characteristics for the pipeline executions. Holds an item
 * per pipeline execution.
 * 
 * @author David Monschein
 *
 */
@Data
public class PerformanceData {
	
	/**
	 * List of performance metrics about the pipeline executions.
	 */
	private List<ExecutionData> executionData;
	
	/**
	 * Creates a new container for the performance data.
	 */
	public PerformanceData() {
		this.executionData = new ArrayList<ExecutionData>();
	}

}
