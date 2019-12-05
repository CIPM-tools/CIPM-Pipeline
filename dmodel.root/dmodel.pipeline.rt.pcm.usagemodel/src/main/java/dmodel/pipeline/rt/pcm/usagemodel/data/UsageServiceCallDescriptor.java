package dmodel.pipeline.rt.pcm.usagemodel.data;

import java.util.List;
import java.util.Map;

import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.parameter.ParameterFactory;
import org.palladiosimulator.pcm.parameter.VariableCharacterisation;
import org.palladiosimulator.pcm.parameter.VariableCharacterisationType;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import com.google.common.collect.Maps;

import de.uka.ipd.sdq.stoex.StoexFactory;
import de.uka.ipd.sdq.stoex.VariableReference;
import dmodel.pipeline.shared.pcm.distribution.DoubleDistribution;
import dmodel.pipeline.shared.pcm.distribution.IntDistribution;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
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

	@SuppressWarnings("unchecked")
	@Override
	public AbstractUserAction toPCM() {
		EntryLevelSystemCall call = UsagemodelFactory.eINSTANCE.createEntryLevelSystemCall();
		call.setOperationSignature__EntryLevelSystemCall(signature);
		call.setProvidedRole_EntryLevelSystemCall(providedRole);

		parameterValues.entrySet().forEach(pv -> {
			VariableUsage varUsage = ParameterFactory.eINSTANCE.createVariableUsage();
			VariableReference varReference = StoexFactory.eINSTANCE.createVariableReference();
			varReference.setReferenceName(pv.getKey());

			// convert to stoex
			PCMRandomVariable characterisation;
			if (pv.getValue().size() > 0) {
				if (pv.getValue().get(0) instanceof Integer) {
					IntDistribution distr = new IntDistribution();
					distr.pushAll((List<Integer>) (List<?>) pv.getValue());
					characterisation = distr.toStochasticExpression();
				} else if (pv.getValue().get(0) instanceof Double) {
					DoubleDistribution distr = new DoubleDistribution(4);
					pv.getValue().forEach(d -> {
						distr.put((double) d);
					});
					characterisation = distr.toStoex();
				} else {
					characterisation = PCMUtils.createRandomVariableFromString("");
				}
			} else {
				characterisation = PCMUtils.createRandomVariableFromString("");
			}

			VariableCharacterisation varCharacter = ParameterFactory.eINSTANCE.createVariableCharacterisation();
			varCharacter.setSpecification_VariableCharacterisation(characterisation);
			varCharacter.setType(VariableCharacterisationType.NUMBER_OF_ELEMENTS);

			varUsage.getVariableCharacterisation_VariableUsage().add(varCharacter);
			varUsage.setNamedReference__VariableUsage(varReference);

			call.getInputParameterUsages_EntryLevelSystemCall().add(varUsage);
		});

		return call;
	}

}
