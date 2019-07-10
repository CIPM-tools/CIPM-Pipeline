package dmodel.pipeline.rt.pcm.usagemodel.test;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import dmodel.pipeline.rt.pcm.usagemodel.UsageModelExtractor;
import dmodel.pipeline.rt.pcm.usagemodel.UsageScenarioBehaviourBuilder;
import dmodel.pipeline.rt.pcm.usagemodel.cluster.SessionCluster;
import dmodel.pipeline.rt.pcm.usagemodel.mapping.MonitoringDataMapping;
import tools.vitruv.applications.pcmjava.modelrefinement.parameters.MonitoringDataSet;
import tools.vitruv.applications.pcmjava.modelrefinement.parameters.impl.KiekerMonitoringReader;
import tools.vitruv.applications.pcmjava.modelrefinement.parameters.util.PcmUtils;

public class UsageModelExtractorTest {

	@Test
	public void test() {
		PcmUtils.loadPCMModels();

		Repository pcmModel = PcmUtils.readFromFile(new File("teastore/pcm/teastore.repository").getAbsolutePath(),
				Repository.class);

		System pcmSystem = PcmUtils.readFromFile(new File("teastore/pcm/teastore.system").getAbsolutePath(),
				System.class);
		UsageModel usage = PcmUtils.readFromFile(new File("teastore/pcm/teastore.usagemodel").getAbsolutePath(),
				UsageModel.class);
		MonitoringDataSet data = new KiekerMonitoringReader("teastore/monitoring/");
		MonitoringDataMapping mapping = new MonitoringDataMapping();
		mapping.addParameterMapping("items.NUMBER_OF_ELEMENTS", "items.NUMBER_OF_ELEMENTS");
		mapping.addParameterMapping("recommender.VALUE", "recommender.VALUE");
		mapping.addParameterMapping("userId.VALUE", "userId.VALUE");

		UsageModelExtractor extractor = new UsageModelExtractor(pcmModel, usage, pcmSystem);
		List<SessionCluster> clusters = extractor.extractUserGroups(data, 0.8f, 5);

		UsageScenarioBehaviourBuilder builder = new UsageScenarioBehaviourBuilder(pcmSystem, pcmModel, mapping);
		UsageModel result = builder.buildFullUsagemodel(clusters);

		PcmUtils.saveToFile(result, "test.usagemodel");
	}

}
