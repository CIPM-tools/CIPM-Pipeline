package dmodel.pipeline.rt.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.rt.validation.data.metric.ValidationMetricType;
import dmodel.pipeline.rt.validation.data.metric.value.DoubleMetricValue;

public class MetricValueTest {

	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			System.out.println(
					mapper.writeValueAsString(new DoubleMetricValue(1.25d, ValidationMetricType.KS_TEST, false)));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
