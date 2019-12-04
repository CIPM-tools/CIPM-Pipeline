package dmodel.pipeline.rt.pcm.usagemodel.data;

import java.util.List;
import java.util.Map;

import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import com.google.common.collect.Maps;

import lombok.Data;

@Data
public class UsageServiceCallDescriptor implements IAbstractUsageDescriptor {
	private String serviceId;
	private Map<String, List<Object>> parameterValues;

	private OperationSignature signature;
	private OperationProvidedRole providedRole;

	public UsageServiceCallDescriptor() {
		this.parameterValues = Maps.newHashMap();
	}

	@Override
	public boolean matches(IAbstractUsageDescriptor other) {
		if (other instanceof UsageServiceCallDescriptor) {
			UsageServiceCallDescriptor uscdOther = (UsageServiceCallDescriptor) other;
			if (uscdOther.serviceId.equals(serviceId)) {
				if (uscdOther.parameterValues.size() == parameterValues.size()) {
					// do not check the parameter values
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void merge(IAbstractUsageDescriptor other) {
		if (other instanceof UsageServiceCallDescriptor) {
			UsageServiceCallDescriptor uscdOther = (UsageServiceCallDescriptor) other;
			if (serviceId.equals(uscdOther.serviceId)) {
				// merge the parameters
				uscdOther.parameterValues.entrySet().forEach(para -> {
					if (parameterValues.containsKey(para.getKey())) {
						// add all values
						parameterValues.get(para.getKey()).addAll(para.getValue());
					} else {
						// easy -> just put it in the map
						parameterValues.put(para.getKey(), para.getValue());
					}
				});
			}
		}
	}

	@Override
	public AbstractUserAction toPCM() {
		EntryLevelSystemCall call = UsagemodelFactory.eINSTANCE.createEntryLevelSystemCall();
		call.setOperationSignature__EntryLevelSystemCall(signature);
		call.setProvidedRole_EntryLevelSystemCall(providedRole);

		return call;
	}

}
