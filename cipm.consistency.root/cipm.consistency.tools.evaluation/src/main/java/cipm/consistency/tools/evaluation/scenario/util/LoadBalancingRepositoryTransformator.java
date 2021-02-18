package cipm.consistency.tools.evaluation.scenario.util;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palladiosimulator.pcm.parameter.ParameterFactory;
import org.palladiosimulator.pcm.parameter.VariableCharacterisation;
import org.palladiosimulator.pcm.parameter.VariableCharacterisationType;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.repository.PrimitiveTypeEnum;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.impl.PrimitiveDataTypeImpl;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.GuardedBranchTransition;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cipm.consistency.base.models.inmodel.InstrumentationModelUtil;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelFactory;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.pcm.util.PCMUtils;
import de.uka.ipd.sdq.stoex.StoexFactory;
import de.uka.ipd.sdq.stoex.VariableReference;

// TODO transform teastore repository to adequate one for loadbalancing
public class LoadBalancingRepositoryTransformator {

	public static void main(String[] argv) {
		PCMUtils.loadPCMModels();
		// establish it for teastore
		LoadBalancingRepositoryTransformator transformer = new LoadBalancingRepositoryTransformator();

		// repository loading
		Repository repository = ModelUtil.readFromFile(new File("teastore_models/init/teastore.repository"),
				Repository.class);

		// establish loadbalancing
		int maxReplications = 3;

		establishAuthLB(transformer, repository, maxReplications);
		establishPersistenceLB(transformer, repository, maxReplications);

		// write back
		File outputRepository = new File("teastore_models/teastore.repository");
		ModelUtil.saveToFile(repository, outputRepository);

		// instrumentation model
		InstrumentationModel out = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();
		enrichInitialInstrumentationModel(out, repository);
		ModelUtil.saveToFile(out, "teastore_models/teastore.inm");
	}

	private static void establishPersistenceLB(LoadBalancingRepositoryTransformator transformer, Repository repository,
			int maxReplications) {
		List<String> signatureIds = Lists.newArrayList("_i1PnIDVXEeqPG_FgW3bi6Q", "_hE4K0DVZEeqPG_FgW3bi6Q",
				"_v-SrkDVYEeqPG_FgW3bi6Q", "_WvtmcDVcEeqPG_FgW3bi6Q", "_XirUcDVcEeqPG_FgW3bi6Q");
		List<String> passThroughExternals = Lists.newArrayList("__VQD0DbfEeq5L_FI-wfNWQ", "_-iMAkDVdEeqPG_FgW3bi6Q",
				"_N_TEQDVdEeqPG_FgW3bi6Q", "_2dMPYDVdEeqPG_FgW3bi6Q");
		List<String> loadBalancingExternals = Lists.newArrayList("_8TRcgDVeEeqPG_FgW3bi6Q", "_-xVaYDVeEeqPG_FgW3bi6Q");

		transformer.establishLoadBalancing(repository, "persistence", signatureIds, passThroughExternals,
				loadBalancingExternals, maxReplications);
	}

	private static void establishAuthLB(LoadBalancingRepositoryTransformator transformer, Repository repository,
			int maxReplications) {
		List<String> signatureIds = Lists.newArrayList("_i1PnIDVXEeqPG_FgW3bi6Q", "_hE4K0DVZEeqPG_FgW3bi6Q",
				"_h3laIDVZEeqPG_FgW3bi6Q", "_ju390DVXEeqPG_FgW3bi6Q");
		List<String> passThroughExternals = Lists.newArrayList("__VQD0DbfEeq5L_FI-wfNWQ", "_CgFBEDbgEeq5L_FI-wfNWQ");
		List<String> loadBalancingExternals = Lists.newArrayList("_-iMAkDVdEeqPG_FgW3bi6Q", "_3PQJADVeEeqPG_FgW3bi6Q");

		transformer.establishLoadBalancing(repository, "auth", signatureIds, passThroughExternals,
				loadBalancingExternals, maxReplications);
	}

	private Map<String, List<String>> replacedExternalCalls = Maps.newHashMap();

	public void establishLoadBalancing(Repository repository, String componentName, List<String> signatureIds,
			List<String> passThroughExternalActionIds, List<String> loadBalancingExternalActionIds, int maxReplicas) {
		System.out.println("Establish for '" + componentName + "'.");
		Map<String, List<List<String>>> nExternalActionIds = this.establishLoadbalancingForComponent(repository,
				componentName, signatureIds, passThroughExternalActionIds, loadBalancingExternalActionIds, maxReplicas);

		// generate serialization strings
		StringBuilder serializationBuilder = new StringBuilder();
		nExternalActionIds.entrySet().forEach(e -> {
			serializationBuilder.append("put(\"" + e.getKey() + "\", new String[][] {");
			for (int i = 0; i < maxReplicas; i++) {
				if (i != 0) {
					serializationBuilder.append(", ");
				}
				serializationBuilder.append("new String[] {");
				for (int k = 0; k < maxReplicas; k++) {
					if (k != 0) {
						serializationBuilder.append(", ");
					}
					if (k <= i) {
						serializationBuilder.append("\"" + e.getValue().get(i).get(k) + "\"");
					} else {
						serializationBuilder.append("null");
					}
				}
				serializationBuilder.append("}");
			}
			serializationBuilder.append("});");
			serializationBuilder.append("\n");
		});
		System.out.println(serializationBuilder.toString());

		nExternalActionIds.entrySet().forEach(e -> {
			Set<String> allSet = Sets.newHashSet();
			e.getValue().forEach(l -> l.forEach(v -> {
				allSet.add(v);
			}));
			replacedExternalCalls.put(e.getKey(), Lists.newArrayList(allSet));
		});
	}

	public Map<String, List<List<String>>> establishLoadbalancingForComponent(Repository repository,
			String componentName, List<String> signatureIds, List<String> passThroughExternalActionIds,
			List<String> loadBalancingExternalActionIds, int maxReplicas) {
		// prepare result
		Map<String, List<List<String>>> resultMap = Maps.newHashMap();

		// get types repo
		Repository typesRepo = PCMUtils.getDefaultPrimitiveTypes();
		DataType intType = typesRepo.getDataTypes__Repository().stream().filter(d -> {
			if (d instanceof PrimitiveDataTypeImpl) {
				PrimitiveDataTypeImpl pd = (PrimitiveDataTypeImpl) d;
				return pd.getType() == PrimitiveTypeEnum.INT;
			}
			return false;
		}).findFirst().orElse(null);

		// 1. create
		String parameterName = componentName + "Reps";
		for (String signatureId : signatureIds) {
			OperationSignature signature = PCMUtils.getElementById(repository, OperationSignature.class, signatureId);
			Parameter nParameter = RepositoryFactory.eINSTANCE.createParameter();
			nParameter.setDataType__Parameter(intType);
			nParameter.setParameterName(parameterName);
			signature.getParameters__OperationSignature().add(nParameter);
		}

		// 2.0 -> check if we already changed one
		List<String> finalPassThroughExternalIds = Lists.newArrayList();
		for (String extId : passThroughExternalActionIds) {
			if (replacedExternalCalls.containsKey(extId)) {
				finalPassThroughExternalIds.addAll(replacedExternalCalls.get(extId));
			} else {
				finalPassThroughExternalIds.add(extId);
			}
		}

		// 2. pass through external adjust
		for (String extId : finalPassThroughExternalIds) {
			ExternalCallAction action = PCMUtils.getElementById(repository, ExternalCallAction.class, extId);
			VariableUsage nUsage = ParameterFactory.eINSTANCE.createVariableUsage();
			VariableReference ref = StoexFactory.eINSTANCE.createVariableReference();
			ref.setReferenceName(parameterName);
			nUsage.setNamedReference__VariableUsage(ref);
			VariableCharacterisation characterization = ParameterFactory.eINSTANCE.createVariableCharacterisation();
			characterization.setType(VariableCharacterisationType.VALUE);
			characterization.setSpecification_VariableCharacterisation(
					PCMUtils.createRandomVariableFromString(parameterName + ".VALUE"));
			nUsage.getVariableCharacterisation_VariableUsage().add(characterization);
			action.getInputVariableUsages__CallAction().add(nUsage);
		}

		// 3. establish load balancing as it
		Map<String, List<OperationRequiredRole>> presentRoles = Maps.newHashMap();
		for (String extId : loadBalancingExternalActionIds) {
			// output list
			List<List<String>> introducedExternalIds = Lists.newArrayList();

			ExternalCallAction action = PCMUtils.getElementById(repository, ExternalCallAction.class, extId);

			List<OperationRequiredRole> loadBalancedRoles;

			if (!presentRoles.containsKey(
					action.getRole_ExternalService().getRequiredInterface__OperationRequiredRole().getId())) {
				OperationRequiredRole singleRole = action.getRole_ExternalService();
				loadBalancedRoles = Lists.newArrayList(singleRole);
				// next step -> duplicate the role
				for (int i = 1; i < maxReplicas; i++) {
					OperationRequiredRole nRole = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
					nRole.setRequiredInterface__OperationRequiredRole(
							singleRole.getRequiredInterface__OperationRequiredRole());
					nRole.setEntityName(singleRole.getEntityName() + "_" + (i + 1));
					singleRole.getRequiringEntity_RequiredRole().getRequiredRoles_InterfaceRequiringEntity().add(nRole);
					loadBalancedRoles.add(nRole);
				}
				presentRoles.put(action.getRole_ExternalService().getRequiredInterface__OperationRequiredRole().getId(),
						loadBalancedRoles);
			} else {
				loadBalancedRoles = presentRoles
						.get(action.getRole_ExternalService().getRequiredInterface__OperationRequiredRole().getId());
			}

			// now create the branches
			BranchAction outerBranch = SeffFactory.eINSTANCE.createBranchAction();
			for (int i = 1; i <= maxReplicas; i++) {
				List<String> innerExternalActionIds = Lists.newArrayList();
				GuardedBranchTransition transition = SeffFactory.eINSTANCE.createGuardedBranchTransition();
				transition.setBranchCondition_GuardedBranchTransition(
						PCMUtils.createRandomVariableFromString(parameterName + ".VALUE == " + i));

				StartAction innerStart = SeffFactory.eINSTANCE.createStartAction();
				BranchAction innerBranch = SeffFactory.eINSTANCE.createBranchAction();
				StopAction innerStop = SeffFactory.eINSTANCE.createStopAction();

				innerStart.setSuccessor_AbstractAction(innerBranch);
				innerBranch.setSuccessor_AbstractAction(innerStop);

				transition
						.setBranchBehaviour_BranchTransition(SeffFactory.eINSTANCE.createResourceDemandingBehaviour());

				transition.getBranchBehaviour_BranchTransition().getSteps_Behaviour().add(innerStart);
				transition.getBranchBehaviour_BranchTransition().getSteps_Behaviour().add(innerBranch);
				transition.getBranchBehaviour_BranchTransition().getSteps_Behaviour().add(innerStop);

				// fill branch
				double branchProbability = (1d / (double) i);
				for (int k = 1; k <= i; k++) {
					ProbabilisticBranchTransition innerTransition = SeffFactory.eINSTANCE
							.createProbabilisticBranchTransition();
					innerTransition.setBranchProbability(branchProbability);

					StartAction innerinnerStart = SeffFactory.eINSTANCE.createStartAction();

					ExternalCallAction innerinnerCall = SeffFactory.eINSTANCE.createExternalCallAction();
					innerinnerCall.setCalledService_ExternalService(action.getCalledService_ExternalService());
					innerinnerCall.setRole_ExternalService(loadBalancedRoles.get(k - 1));

					// copy parameters
					for (VariableUsage usage : action.getInputVariableUsages__CallAction()) {
						VariableUsage usagecopy = ParameterFactory.eINSTANCE.createVariableUsage();
						VariableReference ref = StoexFactory.eINSTANCE.createVariableReference();
						ref.setReferenceName(usage.getNamedReference__VariableUsage().getReferenceName());
						usagecopy.setNamedReference__VariableUsage(ref);

						for (VariableCharacterisation charactcopy : usage.getVariableCharacterisation_VariableUsage()) {
							VariableCharacterisation charact = ParameterFactory.eINSTANCE
									.createVariableCharacterisation();
							charact.setType(charactcopy.getType());
							charact.setSpecification_VariableCharacterisation(PCMUtils.createRandomVariableFromString(
									charactcopy.getSpecification_VariableCharacterisation().getSpecification()));
							usagecopy.getVariableCharacterisation_VariableUsage().add(charact);
						}
						innerinnerCall.getInputVariableUsages__CallAction().add(usagecopy);
					}
					innerExternalActionIds.add(innerinnerCall.getId());

					StopAction innerinnerStop = SeffFactory.eINSTANCE.createStopAction();

					innerinnerStart.setSuccessor_AbstractAction(innerinnerCall);
					innerinnerCall.setSuccessor_AbstractAction(innerinnerStop);

					innerTransition.setBranchBehaviour_BranchTransition(
							SeffFactory.eINSTANCE.createResourceDemandingBehaviour());

					innerTransition.getBranchBehaviour_BranchTransition().getSteps_Behaviour().add(innerinnerStart);
					innerTransition.getBranchBehaviour_BranchTransition().getSteps_Behaviour().add(innerinnerCall);
					innerTransition.getBranchBehaviour_BranchTransition().getSteps_Behaviour().add(innerinnerStop);

					innerBranch.getBranches_Branch().add(innerTransition);
				}

				outerBranch.getBranches_Branch().add(transition);

				introducedExternalIds.add(innerExternalActionIds);
			}
			outerBranch.setPredecessor_AbstractAction(action.getPredecessor_AbstractAction());
			outerBranch.setSuccessor_AbstractAction(action.getSuccessor_AbstractAction());
			action.getResourceDemandingBehaviour_AbstractAction().getSteps_Behaviour().add(outerBranch);
			action.getResourceDemandingBehaviour_AbstractAction().getSteps_Behaviour().remove(action);

			resultMap.put(extId, introducedExternalIds);
		}

		return resultMap;
	}

	private static void enrichInitialInstrumentationModel(InstrumentationModel instrumentationModel,
			Repository repository) {
		// create an initial instrumentation model
		if (instrumentationModel != null && instrumentationModel.getPoints().size() == 0) {
			InstrumentationModelUtil.enrichInitialInstrumentationModel(instrumentationModel, repository);
		}
	}

}
