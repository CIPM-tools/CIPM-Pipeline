package dmodel.pipeline.rt.rest.dt;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.uka.ipd.sdq.identifier.Identifier;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.rest.dt.data.JsonRepository;
import dmodel.pipeline.rt.rest.dt.data.JsonRepositoryComponent;
import dmodel.pipeline.rt.rest.dt.data.JsonRepositoryInterface;
import dmodel.pipeline.shared.JsonUtil;

@RestController
public class SystemRepositoryRestController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RuntimePipelineBlackboard blackboard;

	@GetMapping("/design/system/repository/get")
	public String getGraph() {
		try {
			return objectMapper.writeValueAsString(convertRepositoryToJson());
		} catch (JsonProcessingException e) {
			return JsonUtil.emptyObject();
		}
	}

	private JsonRepository convertRepositoryToJson() {
		JsonRepository output = new JsonRepository();
		Repository parent = blackboard.getPcmQuery().getRaw().getRepository();

		List<JsonRepositoryComponent> components = parent.getComponents__Repository().stream()
				.map(comp -> new JsonRepositoryComponent(comp.getId(), comp.getEntityName(),
						mapToIds(mapToInterfacesA(comp.getProvidedRoles_InterfaceProvidingEntity())),
						mapToIds(mapToInterfacesB(comp.getRequiredRoles_InterfaceRequiringEntity()))))
				.collect(Collectors.toList());

		List<JsonRepositoryInterface> interfaces = parent.getInterfaces__Repository().stream()
				.map(iface -> new JsonRepositoryInterface(iface.getId(), iface.getEntityName()))
				.collect(Collectors.toList());

		output.setComponents(components);
		output.setInterfaces(interfaces);

		return output;
	}

	private List<OperationInterface> mapToInterfacesA(EList<ProvidedRole> providedRoles) {
		return providedRoles.stream().filter(p -> p instanceof OperationProvidedRole)
				.map(OperationProvidedRole.class::cast).map(opr -> opr.getProvidedInterface__OperationProvidedRole())
				.collect(Collectors.toList());
	}

	private List<OperationInterface> mapToInterfacesB(EList<RequiredRole> requiredRoles) {
		return requiredRoles.stream().filter(r -> r instanceof OperationRequiredRole)
				.map(OperationRequiredRole.class::cast).map(orr -> orr.getRequiredInterface__OperationRequiredRole())
				.collect(Collectors.toList());
	}

	private List<String> mapToIds(List<? extends Identifier> list) {
		return list.stream().map(e -> e.getId()).collect(Collectors.toList());
	}

}
