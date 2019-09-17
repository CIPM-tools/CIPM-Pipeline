package dmodel.pipeline.rt.rest.dt.data;

import org.palladiosimulator.pcm.system.System;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.pipeline.rt.rest.dt.SystemBuildRestController;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.pcm.PCMUtils;

public class SerializationTests {

	public static void main(String[] args) {
		PCMUtils.loadPCMModels();

		SystemBuildRestController controller = new SystemBuildRestController();
		System sys = ModelUtil.readFromFile(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.dt.system/output/test1.system",
				System.class);

		ObjectMapper mapper = new ObjectMapper();
		try {
			java.lang.System.out.println(mapper.writeValueAsString(controller.convertSystem(sys)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}

}