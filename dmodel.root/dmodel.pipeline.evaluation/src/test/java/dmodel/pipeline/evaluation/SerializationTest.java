package dmodel.pipeline.evaluation;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import dmodel.pipeline.evaluation.data.ExecutionData;

public class SerializationTest {
	private static ObjectMapper objectMapper;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		objectMapper = new ObjectMapper();
	}

	@Test
	public void test() {
		ExecutionData data = new ExecutionData();
		data.trackPoint(ExecutionMeasuringPoint.T_PRE_FILTER);
		data.trackPoint(ExecutionMeasuringPoint.T_PRE_FILTER);

		try {
			String ret = objectMapper.writeValueAsString(data);

			System.out.println(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
