package cipm.consistency.runtime.pipeline.instrumentation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.pcm.headless.shared.data.results.MeasuringPointType;
import org.pcm.headless.shared.data.results.PlainMeasuringPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import cipm.consistency.base.core.ISpecificModelProvider;
import cipm.consistency.base.core.config.ConfigurationContainer;
import cipm.consistency.base.core.config.PredicateRuleConfiguration;
import cipm.consistency.base.core.state.ExecutionMeasuringPoint;
import cipm.consistency.base.core.state.ValidationSchedulePoint;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;
import cipm.consistency.base.shared.pipeline.PortIDs;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.AbstractIterativePipelinePart;
import cipm.consistency.runtime.pipeline.annotation.InputPort;
import cipm.consistency.runtime.pipeline.annotation.InputPorts;
import cipm.consistency.runtime.pipeline.blackboard.RuntimePipelineBlackboard;
import cipm.consistency.runtime.pipeline.data.PartitionedMonitoringData;
import cipm.consistency.runtime.pipeline.instrumentation.predicate.ValidationPredicate;
import cipm.consistency.runtime.pipeline.instrumentation.predicate.config.ELogicalOperator;
import cipm.consistency.runtime.pipeline.instrumentation.predicate.config.ENumericalComparator;
import cipm.consistency.runtime.pipeline.instrumentation.predicate.impl.DoubleMetricValidationPredicate;
import cipm.consistency.runtime.pipeline.instrumentation.predicate.impl.StackedValidationPredicate;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;
import cipm.consistency.runtime.pipeline.validation.data.metric.ValidationMetricType;
import lombok.extern.java.Log;

@Log
@Service
public class InstrumentationModelTransformation extends AbstractIterativePipelinePart<RuntimePipelineBlackboard> {
	private static final Map<String, ValidationMetricType> predicateMetricMapping = new HashMap<String, ValidationMetricType>() {
		/**
		 * Generated Serial UID.
		 */
		private static final long serialVersionUID = 1634402137017012232L;
		{
			put("kstest", ValidationMetricType.KS_TEST);
			put("avg_rel", ValidationMetricType.AVG_DISTANCE_REL);
			put("avg_absolute", ValidationMetricType.AVG_DISTANCE_ABS);
			put("wasserstein", ValidationMetricType.WASSERSTEIN);
			put("median_absolute", ValidationMetricType.MEDIAN_DISTANCE);
		}
	};

	public InstrumentationModelTransformation() {
		super(ExecutionMeasuringPoint.T_INSTRUMENTATION_MODEL, null);
	}

	@Autowired
	private ConfigurationContainer configuration;

	@Autowired
	private ISpecificModelProvider specificModels;

	@InputPorts({ @InputPort(PortIDs.T_FINAL_INM) })
	public ValidationData adjustInstrumentationModel(PartitionedMonitoringData<PCMContextRecord> records) {
		ValidationData validation = getBlackboard().getValidationResultsQuery().get(ValidationSchedulePoint.FINAL);
		if (validation.isEmpty()) {
			super.trackStart();
			super.trackEnd();
			return validation;
		}

		super.trackStart();

		Set<String> deInstrumentServices = Sets.newHashSet();
		Set<String> instrumentServices = Sets.newHashSet();

		ValidationPredicate validationPredicate = toJavaPredicate(configuration.getValidationPredicates());
		validation.getValidationPoints().forEach(validationPoint -> {
			if (isServiceMeasuringPoint(validationPoint.getMeasuringPoint())) {
				String serviceId = validationPoint.getServiceId();
				if (serviceId != null && validationPoint.getMetricValues().size() > 0) {
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

		// check whether target service is accurate
		if (configuration.getVfl().getTargetServiceId() != null
				&& !configuration.getVfl().getTargetServiceId().isEmpty()) {
			if (deInstrumentServices.contains(configuration.getVfl().getTargetServiceId())) {
				deInstrumentServices.addAll(instrumentServices);
				instrumentServices.clear();
			}
		}

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

		return validation;
	}

	private void changeInstrumentationModel(String deInstr, boolean b) {
		ServiceEffectSpecification seff = getBlackboard().getPcmQuery().getRepository().getServiceById(deInstr);
		Optional<ServiceInstrumentationPoint> instrPoint = getBlackboard().getVsumQuery()
				.getCorrespondingInstrumentationPoint(seff);
		if (instrPoint.isPresent()) {
			instrPoint.get().getActionInstrumentationPoints().forEach(actionPoint -> actionPoint.setActive(b));
		} else {
			instrPoint = specificModels.getInstrumentation().getPoints().stream()
					.filter(p -> p instanceof ServiceInstrumentationPoint).map(ServiceInstrumentationPoint.class::cast)
					.filter(a -> a.getService().getId().equals(deInstr)).findFirst();
			if (instrPoint.isPresent()) {
				instrPoint.get().getActionInstrumentationPoints().forEach(actionPoint -> actionPoint.setActive(b));
			}
		}
	}

	private boolean isServiceMeasuringPoint(PlainMeasuringPoint measuringPoint) {
		if (measuringPoint.getType() == MeasuringPointType.ASSEMBLY_OPERATION
				|| measuringPoint.getType() == MeasuringPointType.ENTRY_LEVEL_CALL) {
			return true;
		}
		return false;
	}

	private ValidationPredicate toJavaPredicate(PredicateRuleConfiguration config) {
		if (config.getCondition() != null) {
			// it is stacked
			ValidationPredicate[] predicates = new ValidationPredicate[config.getRules().size()];

			for (int i = 0; i < config.getRules().size(); i++) {
				predicates[i] = toJavaPredicate(config.getRules().get(i));
			}

			return new StackedValidationPredicate(ELogicalOperator.fromString(config.getCondition()), predicates);
		} else {
			return buildSimpleJavaPredicate(config);
		}
	}

	private ValidationPredicate buildSimpleJavaPredicate(PredicateRuleConfiguration config) {
		ENumericalComparator comparator = ENumericalComparator.fromString(config.getOperator());
		if (comparator != null && predicateMetricMapping.containsKey(config.getId())) {
			return new DoubleMetricValidationPredicate(predicateMetricMapping.get(config.getId()), comparator,
					config.getValue());
		} else {
			log.warning("Metric of type '" + config.getId() + "' or comparator '" + comparator.getName()
					+ "' is not supported yet for the predicate generation.");
		}

		return null;
	}

}
