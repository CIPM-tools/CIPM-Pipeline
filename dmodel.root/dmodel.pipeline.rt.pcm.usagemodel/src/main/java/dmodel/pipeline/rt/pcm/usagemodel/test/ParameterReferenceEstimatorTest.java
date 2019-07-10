package dmodel.pipeline.rt.pcm.usagemodel.test;

import java.io.File;
import java.util.stream.Collectors;

import org.junit.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import dmodel.pipeline.rt.pcm.usagemodel.ParameterRelevanceEstimator;
import tools.vitruv.applications.pcmjava.modelrefinement.parameters.util.PcmUtils;

public class ParameterReferenceEstimatorTest {

	@Test
	public void test() {
		PcmUtils.loadPCMModels();

		Repository pcmModel = PcmUtils.readFromFile(new File("cocome/cocome.repository").getAbsolutePath(),
				Repository.class);
		UsageModel usage = PcmUtils.readFromFile(new File("cocome/cocome.usagemodel").getAbsolutePath(),
				UsageModel.class);

		ResourceDemandingSEFF seff = PcmUtils.getElementById(pcmModel, ResourceDemandingSEFF.class, "bookSale");

		ParameterRelevanceEstimator relEst = new ParameterRelevanceEstimator();
		System.out.println(relEst.getRelevantParameters(seff, 0.05f).stream().map(para -> para.getParameterName())
				.collect(Collectors.toList()));
	}

}
