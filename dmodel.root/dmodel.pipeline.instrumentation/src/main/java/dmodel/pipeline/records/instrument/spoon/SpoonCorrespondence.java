package dmodel.pipeline.records.instrument.spoon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import de.uka.ipd.sdq.identifier.Identifier;
import dmodel.pipeline.models.mapping.MappingFactory;
import dmodel.pipeline.models.mapping.PathMapping;
import dmodel.pipeline.models.mapping.RepositoryMapping;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.path.CtPath;

public class SpoonCorrespondence {

	private Repository repository;
	private CtModel javaModel;

	private Map<CtMethod<?>, ResourceDemandingSEFF> serviceToPCM;
	private Map<ResourceDemandingSEFF, CtMethod<?>> serviceToJava;

	private Map<CtStatement, AbstractAction> actionToPCM;
	private Map<AbstractAction, List<CtStatement>> actionToJava;

	public SpoonCorrespondence(CtModel spoon, Repository repository) {
		this.repository = repository;
		this.javaModel = spoon;

		this.serviceToPCM = new HashMap<>();
		this.serviceToJava = new HashMap<>();

		this.actionToPCM = new HashMap<>();
		this.actionToJava = new HashMap<>();
	}

	public void linkService(CtMethod<?> method, ResourceDemandingSEFF seff) {
		serviceToPCM.put(method, seff);
		serviceToJava.put(seff, method);
	}

	public void linkAbstractAction(List<CtStatement> statements, AbstractAction action) {
		actionToJava.put(action, statements);
		statements.forEach(st -> {
			actionToPCM.put(st, action);
		});
	}

	public Set<Entry<AbstractAction, List<CtStatement>>> getActionMappingEntries() {
		return actionToJava.entrySet();
	}

	public Set<Entry<CtMethod<?>, ResourceDemandingSEFF>> getServiceMappingEntries() {
		return serviceToPCM.entrySet();
	}

	public List<CtStatement> resolveStatements(AbstractAction action) {
		return actionToJava.get(action);
	}

	public AbstractAction resolveAbstractAction(CtStatement statement) {
		return actionToPCM.get(statement);
	}

	public CtMethod<?> resolveMethod(ResourceDemandingSEFF seff) {
		return serviceToJava.get(seff);
	}

	public ResourceDemandingSEFF resolveService(CtMethod<?> method) {
		return serviceToPCM.get(method);
	}

	public RepositoryMapping toMappingModel() {
		RepositoryMapping mapping = MappingFactory.eINSTANCE.createRepositoryMapping();

		this.serviceToPCM.entrySet().forEach(entry -> {
			mapping.getSpoonToPCM().add(buildPathMapping(entry.getKey().getPath(), entry.getValue()));
		});

		this.actionToPCM.entrySet().forEach(entry -> {
			mapping.getSpoonToPCM().add(buildPathMapping(entry.getKey().getPath(), entry.getValue()));
		});

		return mapping;
	}

	private PathMapping buildPathMapping(CtPath path, Identifier id) {
		PathMapping mp = MappingFactory.eINSTANCE.createPathMapping();
		mp.setPcmElementId(id.getId());
		mp.setSpoonPath(path.toString());
		return mp;
	}

}
