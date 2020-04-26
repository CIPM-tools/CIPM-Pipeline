package dmodel.runtime.pipeline.inm.transformation;

import java.util.Optional;
import java.util.Set;

import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.pcm.headless.shared.data.results.MeasuringPointType;
import org.pcm.headless.shared.data.results.PlainMeasuringPoint;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import dmodel.base.core.evaluation.ExecutionMeasuringPoint;
import dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint;
import dmodel.base.shared.pipeline.PortIDs;
import dmodel.runtime.pipeline.AbstractIterativePipelinePart;
import dmodel.runtime.pipeline.annotation.InputPort;
import dmodel.runtime.pipeline.annotation.InputPorts;
import dmodel.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.runtime.pipeline.inm.transformation.predicate.ValidationPredicate;
import dmodel.runtime.pipeline.validation.data.ValidationData;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
@Service
public class InstrumentationModelTransformation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {

	public InstrumentationModelTransformation() {
		super(ExecutionMeasuringPoint.T_INSTRUMENTATION_MODEL, null);
	}

	@Getter
	@Setter
	private ValidationPredicate validationPredicate;

	@InputPorts({ @InputPort(PortIDs.T_VAL_IMM) })
	public void adjustInstrumentationModel(ValidationData validation) {
		if (validation.isEmpty()) {
			super.trackStart();
			super.trackEnd();
			return;
		}

		super.trackStart();

		Set<String> deInstrumentServices = Sets.newHashSet();
		Set<String> instrumentServices = Sets.newHashSet();

		validation.getValidationPoints().forEach(validationPoint -> {
			if (isServiceMeasuringPoint(validationPoint.getMeasuringPoint())) {
				String serviceId = validationPoint.getServiceId();
				if (serviceId != null) {
					boolean fulfilled = validationPredicate == null || validationPredicate.satisfied(validationPoint);

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

		super.trackEnd();
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
