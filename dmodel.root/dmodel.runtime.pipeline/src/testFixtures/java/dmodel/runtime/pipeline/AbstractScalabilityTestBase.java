package dmodel.runtime.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.base.core.health.HealthState;
import dmodel.base.evaluation.PerformanceEvaluation;
import dmodel.base.vsum.VsumManagerTestBase;
import dmodel.designtime.monitoring.records.PCMContextRecord;
import dmodel.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.runtime.pipeline.scalability.ScalabilityMonitoringDataGenerator;
import lombok.extern.java.Log;

@RunWith(SpringRunner.class)
@Import(BasePipelineTestConfiguration.TestContextConfiguration.class)
@Log
public abstract class AbstractScalabilityTestBase extends VsumManagerTestBase {
	@Autowired
	protected RuntimePipelineBlackboard blackboard;

	@Autowired
	private PerformanceEvaluation performanceEval;

	@Autowired
	private ObjectMapper objectMapper;

	@Before
	public void prepareTest() {
		performanceEval.enterPipelineExecution();
		blackboard.reset(true);
	}

	@After
	public void endMeasuring() {
		performanceEval.exitPipelineExecution(HealthState.WORKING);
	}

	public List<PCMContextRecord> generateMonitoringData(ScalabilityMonitoringDataGenerator generator,
			int recordCount) {
		return generator.generateMonitoringData(recordCount);
	}

	// SCALABILITY RUNNER & HELPER
	protected void createPlot(Map<Integer, Long> stats, String outputFile, String scenarioName, String xAxis,
			String yAxis, int xScale) {
		XYSeries dataset = new XYSeries("Transformation Execution Time");
		stats.entrySet().forEach(st -> {
			int reducedKey = (st.getKey() / xScale);
			double reducedValue = (double) st.getValue() / 1000000000d;
			dataset.add(reducedKey, reducedValue);
		});

		XYSeriesCollection data = new XYSeriesCollection(dataset);
		JFreeChart lineChart = ChartFactory.createXYLineChart(scenarioName, xAxis, yAxis, data,
				PlotOrientation.VERTICAL, true, true, false);
		try {
			ChartUtils.saveChartAsPNG(new File(outputFile), lineChart, 800, 600);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void saveRawData(Map<Integer, Long> stats, String outputFile) {
		try {
			objectMapper.writeValue(new File(outputFile), stats);
		} catch (IOException e) {
			log.warning("Failed to save raw measurement data.");
		}
	}

}
