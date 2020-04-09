package dmodel.pipeline.rt.imm.transformation;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.pcm.headless.shared.data.results.MeasuringPointType;
import org.pcm.headless.shared.data.results.PlainMeasuringPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import dmodel.pipeline.core.evaluation.ExecutionMeasuringPoint;
import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint;
import dmodel.pipeline.rt.imm.transformation.predicate.ValidationPredicate;
import dmodel.pipeline.rt.pipeline.AbstractIterativePipelinePart;
import dmodel.pipeline.rt.pipeline.annotation.InputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.rt.validation.data.ValidationData;
import dmodel.pipeline.shared.pipeline.PortIDs;
import lombok.extern.java.Log;

@Log
@Service
public class InstrumentationModelTransformation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	@Autowired
	private List<ValidationPredicate> validationPredicates;

	@InputPorts({ @InputPort(PortIDs.T_VAL_IMM) })
	public void adjustInstrumentationModel(ValidationData validation) {
		if (validation.isEmpty()) {
			getBlackboard().getQuery().track(ExecutionMeasuringPoint.T_INSTRUMENTATION_MODEL);
			return;
		}

		getBlackboard().getQuery().track(ExecutionMeasuringPoint.T_INSTRUMENTATION_MODEL);

		Set<String> deInstrumentServices = Sets.newHashSet();
		Set<String> instrumentServices = Sets.newHashSet();

		validation.getValidationPoints().forEach(validationPoint -> {
			if (isServiceMeasuringPoint(validationPoint.getMeasuringPoint())) {
				String serviceId = validationPoint.getServiceId();
				if (serviceId != null) {
					boolean fulfilled = validationPredicates.stream().allMatch(predicate -> {
						return predicate.satisfied(validationPoint);
					});

					if (fulfilled) {
						deInstrumentServices.add(serviceId);
					} else {
						instrumentServices.add(serviceId);
					}
				}
			}
		});

		// if we have a component on multiple hosts
		// and it is not accurate on a single one
		deInstrumentServices.removeAll(instrumentServices);

		// perform changes
		deInstrumentServices.forEach(deInstr -> {
			log.info("Enable coarse grained monitoring for service '" + deInstr + "'.");
			changeInstrumentationModel(deInstr, false);
		});

		instrumentServices.forEach(instr -> {
			log.info("Enable fine grained monitoring for service '" + instr + "'.");
			changeInstrumentationModel(instr, true);
		});

		getBlackboard().getQuery().track(ExecutionMeasuringPoint.T_INSTRUMENTATION_MODEL);
	}

	private void changeInstrumentationModel(String deInstr, boolean b) {
		ServiceEffectSpecification seff = getBlackboard().getPcmQuery().getRepository().getServiceById(deInstr);
		Optional<ServiceInstrumentationPoint> instrPoint = getBlackboard().getVsumQuery()
				.getCorrespondingInstrumentationPoint(seff);
		if (instrPoint.isPresent()) {
			instrPoint.get().getActionInstrumentationPoints().forEach(actionPoint -> actionPoint.setActive(b));
		}
	}

	private boolean isServiceMeasuringPoint(PlainMeasuringPoint measuringPoint) {
		if (measuringPoint.getType() == MeasuringPointType.ASSEMBLY_OPERATION
				|| measuringPoint.getType() == MeasuringPointType.ENTRY_LEVEL_CALL) {
			return true;
		}
		return false;
	}

}
