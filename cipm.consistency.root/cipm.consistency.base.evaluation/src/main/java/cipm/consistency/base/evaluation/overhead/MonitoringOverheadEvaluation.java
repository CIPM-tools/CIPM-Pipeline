package cipm.consistency.base.evaluation.overhead;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
@Service
public class MonitoringOverheadEvaluation {

	private List<MonitoringOverheadData> container = Lists.newArrayList();

}
