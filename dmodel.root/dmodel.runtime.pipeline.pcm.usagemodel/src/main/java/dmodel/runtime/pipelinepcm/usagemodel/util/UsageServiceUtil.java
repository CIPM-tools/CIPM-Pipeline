package dmodel.runtime.pipelinepcm.usagemodel.util;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import dmodel.base.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.base.core.facade.pcm.ISystemQueryFacade;
import dmodel.designtime.monitoring.records.ServiceCallRecord;
import dmodel.designtime.monitoring.util.ServiceParametersWrapper;
import dmodel.runtime.pipelinepcm.usagemodel.data.IAbstractUsageDescriptor;
import dmodel.runtime.pipelinepcm.usagemodel.data.UsageServiceCallDescriptor;

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

	public static synchronized boolean isEntryCall(IRepositoryQueryFacade repository, ISystemQueryFacade system,
			ServiceCallRecord rec) {
		ResourceDemandingSEFF seff = repository.getServiceById(rec.getServiceId());

		return system.isEntryCall(seff);
	}

	public static UsageServiceCallDescriptor createDescriptor(ServiceCallRecord rec, IRepositoryQueryFacade repository,
			ISystemQueryFacade system) {
		if (rec == null) {
			return null;
		}

		UsageServiceCallDescriptor ret = new UsageServiceCallDescriptor();
		ResourceDemandingSEFF seff = repository.getServiceById(rec.getServiceId());
		ret.setSignature((OperationSignature) seff.getDescribedService__SEFF());
		ret.setProvidedRole(system.getProvidedRoleBySignature(seff.getDescribedService__SEFF()).get(0));

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
