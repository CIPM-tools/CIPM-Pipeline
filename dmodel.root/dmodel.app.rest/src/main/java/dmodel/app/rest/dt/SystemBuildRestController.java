package dmodel.app.rest.dt;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ComposedStructure;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.DelegationConnector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.system.System;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dmodel.app.rest.dt.async.BuildServiceCallGraphProcess;
import dmodel.app.rest.dt.async.StartBuildingSystemProcess;
import dmodel.app.rest.dt.data.scg.JsonProjectFilesResponse;
import dmodel.app.rest.dt.data.scg.JsonServiceCallGraph;
import dmodel.app.rest.dt.data.system.JsonBuildingConflict;
import dmodel.app.rest.dt.data.system.JsonBuildingStartMessage;
import dmodel.app.rest.dt.data.system.JsonConflictSolution;
import dmodel.app.rest.dt.data.system.JsonPCMSystem;
import dmodel.app.rest.dt.data.system.JsonSystemAssembly;
import dmodel.app.rest.dt.data.system.JsonSystemComposite;
import dmodel.app.rest.dt.data.system.JsonSystemConnector;
import dmodel.app.rest.dt.data.system.JsonSystemProvidedRole;
import dmodel.app.rest.dt.data.system.JsonSystemRequiredRole;
import dmodel.base.core.ICallGraphProvider;
import dmodel.base.core.config.ConfigurationContainer;
import dmodel.base.core.facade.IPCMQueryFacade;
import dmodel.base.shared.JsonUtil;
import dmodel.base.shared.pcm.util.PCMUtils;
import dmodel.base.shared.util.StackedRunnable;
import dmodel.designtime.system.pcm.data.AbstractConflict;
import dmodel.designtime.system.pcm.data.AssemblyConflict;
import dmodel.designtime.system.pcm.data.ConnectionConflict;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.AssemblyRequiredRole;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.SystemProvidedRole;
import dmodel.designtime.system.pcm.impl.util.Xor;
import dmodel.designtime.system.scg.ServiceCallGraphBuilder;
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

	@GetMapping("/design/system/scg/jars")
	public String getPossibleJars() {
		if (!configurationContainer.getProject().isValid()) {
			return JsonUtil.wrapAsObject("success", false, false);
		}

		try {
			return objectMapper.writeValueAsString(JsonProjectFilesResponse.from(
					new File(configurationContainer.getProject().getRootPath()), f -> f.getName().endsWith(".jar")));
		} catch (JsonProcessingException e) {
			return JsonUtil.wrapAsObject("success", false, false);
		}
	}

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
			return objectMapper.writeValueAsString(this.convertSystem(systemBuilder.getCurrentSystem()));
		} catch (JsonProcessingException e) {
			return JsonUtil.emptyObject();
		}
	}

	public JsonPCMSystem convertSystem(System system) {
		JsonPCMSystem output = new JsonPCMSystem();

		JsonSystemComposite root = convertComposite(system);

		// roles for parent
		system.getProvidedRoles_InterfaceProvidingEntity().stream().map(p -> this.mapProvidedRole(system.getId(), p))
				.forEach(root.getProvided()::add);
		system.getRequiredRoles_InterfaceRequiringEntity().stream().map(r -> this.mapRequiredRole(system.getId(), r))
				.forEach(root.getRequired()::add);

		output.setRoot(root);

		return output;
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
			return new String[] { target.getLeft().getCtx().getId() + "-" + target.getLeft().getRole().getId() };
		} else if (target.rightPresent()) {
			return new String[] { target.getRight().getSystem().getId() + "-" + target.getRight().getRole().getId() };
		}
		return null;
	}

	private JsonSystemComposite convertComposite(ComposedStructure comp) {
		JsonSystemComposite output = new JsonSystemComposite();

		output.setId(comp.getId());
		output.setName(comp.getEntityName());

		// assemblys
		for (AssemblyContext ctx : comp.getAssemblyContexts__ComposedStructure()) {
			JsonSystemAssembly asmbly = new JsonSystemAssembly();
			asmbly.setComponentId(ctx.getEncapsulatedComponent__AssemblyContext().getId());
			asmbly.setComponentName(ctx.getEncapsulatedComponent__AssemblyContext().getEntityName());
			asmbly.setId(ctx.getId());
			asmbly.setName(ctx.getEntityName());

			// roles
			ctx.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity().stream()
					.map(p -> this.mapProvidedRole(ctx.getId(), p)).forEach(asmbly.getProvided()::add);
			ctx.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity().stream()
					.map(r -> this.mapRequiredRole(ctx.getId(), r)).forEach(asmbly.getRequired()::add);

			output.getAssemblys().add(asmbly);
		}

		// connectors
		for (Connector conn : comp.getConnectors__ComposedStructure()) {
			JsonSystemConnector jsonConn = new JsonSystemConnector();
			jsonConn.setId(conn.getId());
			jsonConn.setName(conn.getEntityName());

			if (conn instanceof AssemblyConnector) {
				jsonConn.setDelegation(false);
				AssemblyConnector aconn = (AssemblyConnector) conn;
				jsonConn.setAssemblyFrom(aconn.getProvidingAssemblyContext_AssemblyConnector().getId());
				jsonConn.setAssemblyTo(aconn.getRequiringAssemblyContext_AssemblyConnector().getId());
				jsonConn.setRole1(aconn.getProvidingAssemblyContext_AssemblyConnector().getId() + "-"
						+ aconn.getProvidedRole_AssemblyConnector().getId());
				jsonConn.setRole2(aconn.getRequiringAssemblyContext_AssemblyConnector().getId() + "-"
						+ aconn.getRequiredRole_AssemblyConnector().getId());
			} else if (conn instanceof DelegationConnector) {
				jsonConn.setDelegation(true);
				if (conn instanceof ProvidedDelegationConnector) {
					ProvidedDelegationConnector dconn = (ProvidedDelegationConnector) conn;
					jsonConn.setDelegationDirection(true);
					jsonConn.setAssemblyTo(dconn.getAssemblyContext_ProvidedDelegationConnector().getId());
					jsonConn.setAssemblyFrom(dconn.getOuterProvidedRole_ProvidedDelegationConnector()
							.getProvidingEntity_ProvidedRole().getId());
					jsonConn.setRole1(dconn.getAssemblyContext_ProvidedDelegationConnector().getId() + "-"
							+ dconn.getInnerProvidedRole_ProvidedDelegationConnector().getId());
					jsonConn.setRole2(
							dconn.getOuterProvidedRole_ProvidedDelegationConnector().getProvidingEntity_ProvidedRole()
									.getId() + "-" + dconn.getOuterProvidedRole_ProvidedDelegationConnector().getId());
				} else if (conn instanceof RequiredDelegationConnector) {
					RequiredDelegationConnector dconn = (RequiredDelegationConnector) conn;
					jsonConn.setDelegationDirection(false);
					jsonConn.setAssemblyFrom(dconn.getAssemblyContext_RequiredDelegationConnector().getId());
					jsonConn.setAssemblyTo(dconn.getOuterRequiredRole_RequiredDelegationConnector()
							.getRequiringEntity_RequiredRole().getId());
					jsonConn.setRole1(dconn.getAssemblyContext_RequiredDelegationConnector().getId() + "-"
							+ dconn.getInnerRequiredRole_RequiredDelegationConnector().getId());
					jsonConn.setRole2(
							dconn.getOuterRequiredRole_RequiredDelegationConnector().getRequiringEntity_RequiredRole()
									.getId() + "-" + dconn.getOuterRequiredRole_RequiredDelegationConnector().getId());
				}
			}

			output.getConnectors().add(jsonConn);
		}

		return output;
	}

	private JsonSystemRequiredRole mapRequiredRole(String prefix, RequiredRole rr) {
		JsonSystemRequiredRole jrr = new JsonSystemRequiredRole();
		jrr.setId(prefix + "-" + rr.getId());
		jrr.setName(rr.getEntityName());
		return jrr;
	}

	private JsonSystemProvidedRole mapProvidedRole(String prefix, ProvidedRole pr) {
		JsonSystemProvidedRole jpr = new JsonSystemProvidedRole();
		jpr.setId(prefix + "-" + pr.getId());
		jpr.setName(pr.getEntityName());
		return jpr;
	}

}
