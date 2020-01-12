package dmodel.pipeline.rt.pipeline.border;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.models.mapping.MappingFactory;
import dmodel.pipeline.models.mapping.MappingPackage;
import dmodel.pipeline.models.mapping.PalladioRuntimeMapping;
import dmodel.pipeline.shared.FileBackedModelUtil;
import dmodel.pipeline.shared.ModelUtil;
import dmodel.pipeline.shared.config.DModelConfigurationContainer;
import lombok.Data;

@Service
@Data
public class RunTimeDesignTimeBorder implements InitializingBean {
	private static final String RT_MAPPING_PATH = "models" + File.separator + "rt_mapping.corr";

	private ServiceCallGraph serviceCallGraph;
	private PalladioRuntimeMapping runtimeMapping;

	private File currentRuntimeMappingPath;

	@Autowired
	private DModelConfigurationContainer config;

	@Override
	public void afterPropertiesSet() throws Exception {
		// load mapping package
		MappingPackage.eINSTANCE.eClass();

		config.getProject().getListeners().add(d -> {
			refreshRuntimeMappingPath();
		});
		refreshRuntimeMappingPath();
	}

	private void refreshRuntimeMappingPath() {
		File rtMappingFile = new File(new File(config.getProject().getRootPath()), RT_MAPPING_PATH);
		if (!rtMappingFile.exists()) {
			PalladioRuntimeMapping nMapping = MappingFactory.eINSTANCE.createPalladioRuntimeMapping();
			ModelUtil.saveToFile(nMapping, rtMappingFile);
		}

		try {
			if (currentRuntimeMappingPath != null
					&& rtMappingFile.getCanonicalPath().equals(currentRuntimeMappingPath.getCanonicalPath())) {
				return;
			}
		} catch (IOException e) {
			return;
		}

		this.runtimeMapping = FileBackedModelUtil.synchronize(this.runtimeMapping, rtMappingFile,
				PalladioRuntimeMapping.class, null, v -> {
					return MappingFactory.eINSTANCE.createPalladioRuntimeMapping();
				});
		currentRuntimeMappingPath = rtMappingFile;
	}

}
