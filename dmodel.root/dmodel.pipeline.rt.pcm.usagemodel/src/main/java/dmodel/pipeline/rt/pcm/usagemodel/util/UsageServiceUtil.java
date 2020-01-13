package dmodel.pipeline.rt.pcm.usagemodel.util;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;
import org.pcm.headless.api.util.PCMUtil;

import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.ServiceParametersWrapper;
import dmodel.pipeline.rt.pcm.usagemodel.data.IAbstractUsageDescriptor;
import dmodel.pipeline.rt.pcm.usagemodel.data.UsageServiceCallDescriptor;
import dmodel.pipeline.shared.pcm.util.PCMUtils;

public class UsageServiceUtil {

	public static Comparator<ServiceCallRecord> COMPARE_SCAL_ENTRY_TIME_ASCENDING = (a, b) -> {
		if (a.getEntryTime() > b.getEntryTime()) {
			return -1;
		} else if (a.getEntryTime() < b.getEntryTime()) {
			return 1;
		} else {
			return 0;
		}
	};

	public static boolean isEntryCall(Repository repository, System system, ServiceCallRecord rec) {
		ResourceDemandingSEFF seff = PCMUtil.getElementById(repository, ResourceDemandingSEFF.class,
				rec.getServiceId());

		return system.getProvidedRoles_InterfaceProvidingEntity().stream().filter(pr -> {
			if (pr instanceof OperationProvidedRole) {
				return ((OperationProvidedRole) pr).getProvidedInterface__OperationProvidedRole()
						.getSignatures__OperationInterface().stream().anyMatch(op -> {
							return op.getId().equals(seff.getDescribedService__SEFF().getId());
						});
			}
			return false;
		}).map(pr -> (OperationProvidedRole) pr).findFirst().isPresent();
	}

	public static UsageServiceCallDescriptor createDescriptor(ServiceCallRecord rec, Repository repository,
			System system) {
		if (rec == null) {
			return null;
		}

		UsageServiceCallDescriptor ret = new UsageServiceCallDescriptor();
		ResourceDemandingSEFF seff = PCMUtils.getElementById(repository, ResourceDemandingSEFF.class,
				rec.getServiceId());
		ret.setSignature((OperationSignature) seff.getDescribedService__SEFF());
		ret.setProvidedRole(system.getProvidedRoles_InterfaceProvidingEntity().stream().filter(pr -> {
			if (pr instanceof OperationProvidedRole) {
				return ((OperationProvidedRole) pr).getProvidedInterface__OperationProvidedRole()
						.getSignatures__OperationInterface().stream().anyMatch(op -> {
							return op.getId().equals(seff.getDescribedService__SEFF().getId());
						});
			}
			return false;
		}).map(pr -> (OperationProvidedRole) pr).findFirst().orElse(null));

		ret.setServiceId(rec.getServiceId());
		for (Entry<String, Object> entry : ServiceParametersWrapper.buildFromJson(rec.getParameters()).getParameters()
				.entrySet()) {
			EList<Object> nList = new BasicEList<>();
			nList.add(entry.getValue());
			ret.getParameterValues().put(entry.getKey(), nList);
		}

		return ret;
	}

	public static ScenarioBehaviour createBehaviour(List<IAbstractUsageDescriptor> descriptors) {
		ScenarioBehaviour behav = UsagemodelFactory.eINSTANCE.createScenarioBehaviour();

		Start startAction = UsagemodelFactory.eINSTANCE.createStart();
		behav.getActions_ScenarioBehaviour().add(startAction);

		Stop stopAction = UsagemodelFactory.eINSTANCE.createStop();

		AbstractUserAction current = startAction;
		for (IAbstractUsageDescriptor desc : descriptors) {
			AbstractUserAction conv = desc.toPCM();
			if (conv != null) {
				current.setSuccessor(conv);
				current = conv;
				behav.getActions_ScenarioBehaviour().add(current);
			}
		}
		current.setSuccessor(stopAction);
		behav.getActions_ScenarioBehaviour().add(stopAction);

		return behav;
	}

}
