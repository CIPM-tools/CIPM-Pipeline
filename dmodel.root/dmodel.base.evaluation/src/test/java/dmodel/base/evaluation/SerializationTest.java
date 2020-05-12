package dmodel.base.evaluation;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.base.core.evaluation.ExecutionMeasuringPoint;
import dmodel.base.evaluation.data.ExecutionData;

public class SerializationTest {
	private static ObjectMapper objectMapper;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		objectMapper = new ObjectMapper();
	}

	@Test
	public void test() throws JsonProcessingException {
		ExecutionData data = new ExecutionData();
		data.trackPoint(ExecutionMeasuringPoint.T_PRE_FILTER);
		data.trackPoint(ExecutionMeasuringPoint.T_PRE_FILTER);

		objectMapper.writeValueAsString(data);
	}

}
