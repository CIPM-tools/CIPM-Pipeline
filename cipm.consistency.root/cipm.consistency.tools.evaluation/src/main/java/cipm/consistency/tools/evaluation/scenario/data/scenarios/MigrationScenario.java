package cipm.consistency.tools.evaluation.scenario.data.scenarios;

import java.util.List;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.google.common.collect.Lists;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenario;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioExecutionConfig;
import cipm.consistency.tools.evaluation.scenario.data.AdaptionScenarioType;
import cipm.consistency.tools.evaluation.scenario.data.teastore.MigrationComponentType;
import cipm.consistency.tools.evaluation.scenario.data.teastore.RecommenderType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;

@Data
@EqualsAndHashCode(callSuper = true)
@Log
public class MigrationScenario extends AdaptionScenario {
	private static final long WAIT_FOR_END_TRAINING = 1500;
	private static final long WAIT_UNTIL_CONTAINER_STOPPED = 5000;
	private static final long WAIT_UNTIL_CONTAINER_UP = 10000;

	private static DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
			.withDockerHost("tcp://localhost:2375").build();
	private static DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();

	private MigrationComponentType component;
	private int instanceId;

	public MigrationScenario(MigrationComponentType type, int instanceId) {
		super(AdaptionScenarioType.MIGRATION);
		this.component = type;
		this.instanceId = instanceId;
	}

	public MigrationScenario() {
		this(null, 0);
	}

	@Override
	public void execute(AdaptionScenarioExecutionConfig config) {
		// stop load
		try {
			Thread.sleep(WAIT_FOR_END_TRAINING);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		List<Container> containers = dockerClient.listContainersCmd().exec();
		int k = 0;
		Container migrateContainer = null;
		for (Container conn : containers) {
			if (conn.getImage().startsWith(component.getName())) {
				if (k == instanceId) {
					migrateContainer = conn;
					break;
				} else {
					k++;
				}
			}
		}

		if (migrateContainer != null) {
			performMigration(migrateContainer);
		} else {
			log.warning("Could not migrate component of type '" + component.getName() + "' with instance id "
					+ instanceId + ".");
		}

		// start load
		try {
			Thread.sleep(WAIT_UNTIL_CONTAINER_UP);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void performMigration(Container migrateContainer) {
		String migrateImageOutput = component.getName() + "-" + migrateContainer.getId().substring(0, 10) + "-"
				+ String.valueOf(System.currentTimeMillis());
		if (migrateContainer != null) {
			// 1. stop
			dockerClient.stopContainerCmd(migrateContainer.getId()).exec();

			// 2. commit
			dockerClient.commitCmd(migrateContainer.getId()).withRepository(migrateImageOutput).exec();

			// 2.1. wait until commited and stopped
			try {
				Thread.sleep(WAIT_UNTIL_CONTAINER_STOPPED);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			String networkToUse = migrateContainer.getNetworkSettings().getNetworks().keySet().stream().findFirst()
					.orElse("compose_default");

			// 3. create new container
			List<PortBinding> bindings = Lists.newArrayList();
			for (ContainerPort port : migrateContainer.getPorts()) {
				if (port.getPublicPort() != null) {
					bindings.add(new PortBinding(Binding.bindPort(port.getPublicPort()),
							new ExposedPort(port.getPrivatePort())));
				} else {
					bindings.add(new PortBinding(null, new ExposedPort(port.getPrivatePort())));
				}
			}

			String createId = dockerClient.createContainerCmd(migrateImageOutput)
					.withName(migrateImageOutput + "-migrated")
					.withHostConfig(HostConfig.newHostConfig().withNetworkMode(networkToUse).withPortBindings(bindings))
					.withEnv("REGISTRY_HOST=registry").exec().getId();

			// 4. start migrated
			dockerClient.startContainerCmd(createId).exec();
		}

	}

	@Override
	public InMemoryPCM generateReferenceModel(InMemoryPCM current) {
		if (this.getComponent() == MigrationComponentType.RECOMMENDER) {
			return new SystemChangeScenario(RecommenderType.SLOPE_ONE).generateReferenceModel(current);
			// migration reverts recommender type
		} else {
			return current;
		}
	}

}
