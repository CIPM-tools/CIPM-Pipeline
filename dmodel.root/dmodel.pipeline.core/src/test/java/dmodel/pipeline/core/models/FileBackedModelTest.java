package dmodel.pipeline.core.models;

import java.io.File;

import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import dmodel.pipeline.shared.FileBackedModelUtil;

public class FileBackedModelTest {

	public static void main(String[] argv) {
		UsageModel usageModel = UsagemodelFactory.eINSTANCE.createUsageModel();

		FileBackedModelUtil.synchronize(usageModel, new File("test.usagemodel"), UsageModel.class, (v) -> {
			System.out.println("Write model to file.");
		}, c -> {
			return UsagemodelFactory.eINSTANCE.createUsageModel();
		});

		usageModel.getUsageScenario_UsageModel().add(UsagemodelFactory.eINSTANCE.createUsageScenario());

		usageModel.getUsageScenario_UsageModel().clear();

		usageModel.getUsageScenario_UsageModel().add(UsagemodelFactory.eINSTANCE.createUsageScenario());
		usageModel.getUsageScenario_UsageModel().add(UsagemodelFactory.eINSTANCE.createUsageScenario());
		usageModel.getUsageScenario_UsageModel().add(UsagemodelFactory.eINSTANCE.createUsageScenario());
	}

}
