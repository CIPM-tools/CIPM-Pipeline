package cipm.consistency.app.rest.dt;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.system.System;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cipm.consistency.app.rest.dt.async.BuildServiceCallGraphProcess;
import cipm.consistency.app.rest.dt.async.StartBuildingSystemProcess;
import cipm.consistency.app.rest.dt.data.scg.JsonServiceCallGraph;
import cipm.consistency.app.rest.dt.data.system.JsonBuildingConflict;
import cipm.consistency.app.rest.dt.data.system.JsonBuildingStartMessage;
import cipm.consistency.app.rest.dt.data.system.JsonConflictSolution;
import cipm.consistency.base.core.ICallGraphProvider;
import cipm.consistency.base.core.config.ConfigurationContainer;
import cipm.consistency.base.core.facade.IPCMQueryFacade;
import cipm.consistency.base.shared.JsonUtil;
import cipm.consistency.base.shared.json.JsonEObject;
import cipm.consistency.base.shared.pcm.util.PCMUtils;
import cipm.consistency.base.shared.util.StackedRunnable;
import cipm.consistency.designtime.systemextraction.pcm.data.AbstractConflict;
import cipm.consistency.designtime.systemextraction.pcm.data.AssemblyConflict;
import cipm.consistency.designtime.systemextraction.pcm.data.ConnectionConflict;
import cipm.consistency.designtime.systemextraction.pcm.impl.PCMSystemBuilder;
import cipm.consistency.designtime.systemextraction.pcm.impl.PCMSystemBuilder.AssemblyRequiredRole;
import cipm.consistency.designtime.systemextraction.pcm.impl.PCMSystemBuilder.SystemProvidedRole;
import cipm.consistency.designtime.systemextraction.pcm.impl.util.Xor;
import cipm.consistency.designtime.systemextraction.scg.ServiceCallGraphBuilder;
import lombok.extern.java.Log;

@RestController
@Log
public class SystemBuildRestController {
	@Autowired
	private PCMSystemBuilder systemBuilder;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ICallGraphProvider callGraphProvider;

	@Autowired
	private ScheduledExecutorService executorService;

	@Autowired
	private IPCMQueryFacade pcmQuery;

	@Autowired
	private ServiceCallGraphBuilder scgBuilder;

	@Autowired
	private ConfigurationContainer configurationContainer;

	@Autowired
	private DesignTimeRestController designTimeRestController;

	// DATA
	private boolean finishedBuilding = true;

	@PostMapping("/design/system/scg/build")
	public String buildSCG(@RequestParam String jarFiles, @RequestParam boolean extractMappingBefore) {
		try {
			List<String> jarFileArray = objectMapper.readValue(jarFiles, new TypeReference<List<String>>() {
			});

			BuildServiceCallGraphProcess process = new BuildServiceCallGraphProcess(scgBuilder, jarFileArray,
					configurationContainer.getProject().getRootPath());

			if (extractMappingBefore) {
				designTimeRestController.resolveMappingFromCode();
			}

			// blocking
			process.run();

			return JsonUtil.wrapAsObject("success", true, false);
		} catch (IOException e) {
			log.warning("Jar files of the project could not be parsed.");
			return JsonUtil.wrapAsObject("success", false, false);
		}
	}

	@GetMapping("/design/system/scg/get")
	public String getSCG() {
		try {
			return objectMapper.writeValueAsString(JsonServiceCallGraph.from(scgBuilder.provideCallGraph()));
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("success", false, false);
		}
	}

	@PostMapping("/design/system/build/start")
	public String buildSystem(@RequestParam String coreInterfaces) {
		finishedBuilding = false;

		try {
			JsonBuildingStartMessage parsedMessage = objectMapper.readValue(coreInterfaces,
					JsonBuildingStartMessage.class);

			List<OperationInterface> systemInterfaces = parsedMessage.getInterfaceIds().stream()
					.map(id -> pcmQuery.getRepository().getOperationInterface(id)).collect(Collectors.toList());

			StartBuildingSystemProcess process2 = new StartBuildingSystemProcess(systemBuilder, callGraphProvider,
					systemInterfaces);
			process2.addListener(conf -> {
				if (conf == null) {
					finishedBuilding = true;
					flushResultingSystem(systemBuilder.getCurrentSystem());
				}
			});

			executorService.submit(new StackedRunnable(true, process2));
		} catch (IOException e) {
			e.printStackTrace();
			return JsonUtil.emptyObject();
		}

		return JsonUtil.emptyObject();
	}

	@GetMapping("/design/system/build/conflict/get")
	public String getConflict() {
		if (!finishedBuilding) {
			JsonBuildingConflict conflict = convertConflict(systemBuilder.getCurrentConflict());
			try {
				return objectMapper.writeValueAsString(conflict);
			} catch (JsonProcessingException e) {
				return JsonUtil.emptyObject();
			}
		} else {
			return JsonUtil.emptyObject();
		}
	}

	@PostMapping("/design/system/build/conflict/solve")
	public String resolveConflict(@RequestParam String solution) {
		try {
			JsonConflictSolution jsolution = objectMapper.readValue(solution, JsonConflictSolution.class);
			AbstractConflict<?> currentConflict = systemBuilder.getCurrentConflict();
			if (currentConflict != null && !finishedBuilding) {
				if (currentConflict instanceof AssemblyConflict) {
					AssemblyContext ctx = ((AssemblyConflict) currentConflict).getSolutions().stream()
							.filter(as -> as.getId().equals(jsolution.getSolution())).findFirst().orElse(null);
					((AssemblyConflict) currentConflict).setSolution(ctx);
				} else if (currentConflict instanceof ConnectionConflict) {
					RepositoryComponent opr = ((ConnectionConflict) currentConflict).getSolutions().stream()
							.filter(pr -> pr.getId().equals(jsolution.getSolution())).findFirst().orElse(null);
					((ConnectionConflict) currentConflict).setSolution(opr);
				}

				// mark as solved
				currentConflict.setSolved(true);

				inheritNameMapping(jsolution.getNameMapping());

				// continue building
				finishedBuilding = systemBuilder.continueBuilding();

				if (finishedBuilding) {
					flushResultingSystem(systemBuilder.getCurrentSystem());
				}
			}
		} catch (IOException e) {
			return JsonUtil.emptyObject();
		}

		return JsonUtil.emptyObject();
	}

	@GetMapping("/design/system/build/status")
	public String getStatus() {
		return JsonUtil.wrapAsObject("status",
				finishedBuilding ? "finished" : (systemBuilder.getCurrentConflict() == null ? "idle" : "conflict"),
				true);
	}

	@GetMapping("/design/system/build/get")
	public String getCurrentSystem() {
		try {
			return objectMapper.writeValueAsString(JsonEObject.create(systemBuilder.getCurrentSystem()));
		} catch (JsonProcessingException e) {
			return JsonUtil.emptyObject();
		}
	}

	private void flushResultingSystem(System currentSystem) {
		pcmQuery.getRaw().swapSystem(currentSystem);
	}

	private void inheritNameMapping(Map<String, String> nameMapping) {
		nameMapping.entrySet().forEach(e -> {
			NamedElement ident = PCMUtils.getElementById(systemBuilder.getCurrentSystem(), NamedElement.class,
					e.getKey());
			if (ident != null) {
				ident.setEntityName(e.getValue());
			} else {
				if (systemBuilder.getCurrentSystem().getId().equals(e.getKey())) {
					systemBuilder.getCurrentSystem().setEntityName(e.getValue());
				}
			}
		});
	}

	private JsonBuildingConflict convertConflict(AbstractConflict<?> conf) {
		JsonBuildingConflict output = new JsonBuildingConflict();
		if (conf instanceof AssemblyConflict) {
			AssemblyConflict aconf = (AssemblyConflict) conf;
			output.setId(aconf.getId());
			output.setType("assembly");
			output.setPossibleIds(aconf.getSolutions().stream().map(a -> a.getId()).toArray(String[]::new));
			output.setTargetIds(generateTargetIds(((AssemblyConflict) conf).getTarget()));
		} else if (conf instanceof ConnectionConflict) {
			ConnectionConflict cconf = (ConnectionConflict) conf;
			output.setId(cconf.getId());
			output.setType("connection");
			output.setPossibleIds(cconf.getSolutions().stream().map(a -> a.getId()).toArray(String[]::new));
			output.setTargetIds(generateTargetIds(((ConnectionConflict) conf).getTarget()));
		}
		return output;
	}

	private String[] generateTargetIds(Xor<AssemblyRequiredRole, SystemProvidedRole> target) {
		if (target.leftPresent()) {
			return new String[] { target.getLeft().getCtx().getId() + "###" + target.getLeft().getRole().getId() };
		} else if (target.rightPresent()) {
			return new String[] { target.getRight().getSystem().getId() + "###" + target.getRight().getRole().getId() };
		}
		return null;
	}

}
