package dmodel.pipeline.rt.rest.dt.data;

import org.palladiosimulator.pcm.system.System;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.app.rest.dt.SystemBuildRestController;
import dmodel.base.shared.ModelUtil;
import dmodel.base.shared.pcm.util.PCMUtils;

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
