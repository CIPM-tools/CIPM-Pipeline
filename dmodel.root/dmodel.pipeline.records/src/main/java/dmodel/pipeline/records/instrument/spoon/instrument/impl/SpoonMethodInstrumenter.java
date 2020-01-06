package dmodel.pipeline.records.instrument.spoon.instrument.impl;

import java.util.ArrayList;
import java.util.List;

import InstrumentationMetamodel.ServiceInstrumentationPoint;
import dmodel.pipeline.monitoring.controller.ServiceParameters;
import dmodel.pipeline.monitoring.controller.ThreadMonitoringController;
import dmodel.pipeline.records.instrument.spoon.instrument.ISpoonInstrumenter;
import spoon.Launcher;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

public class SpoonMethodInstrumenter implements ISpoonInstrumenter<CtMethod<?>> {
	private static final String SERVICE_PARAMETER_VARIABLE = "serviceParametersMonitoring";
	private static final String THREAD_MONITORING_CONTROLLER_VARIABLE = "threadMonitoringController";

	@Override
	public void instrument(Launcher parent, CtMethod<?> target, ServiceInstrumentationPoint probe) {
		Factory factory = parent.getFactory();

		if (probe.isActive()) {
			// instrument the method

			// 1.0. get thread monitoring controller
			CtExecutableReference<?> getInstance = factory.Type().get(ThreadMonitoringController.class)
					.getMethodsByName("getInstance").get(0).getReference();
			CtInvocation<?> invocGet = factory.createInvocation(factory.createLiteral(ThreadMonitoringController.class),
					getInstance);
			CtLocalVariable<?> threadMonitoringVariable = factory.createLocalVariable(
					factory.createReference(ThreadMonitoringController.class.getName()),
					THREAD_MONITORING_CONTROLLER_VARIABLE, invocGet);

			// 1.1. build empty parameter container
			CtTypeReference<ServiceParameters> typeReferenceParams = factory
					.createReference(ServiceParameters.class.getName());
			CtConstructorCall<ServiceParameters> constructorCall = factory.createConstructorCall(typeReferenceParams);
			CtLocalVariable<ServiceParameters> nVariable = factory.createLocalVariable(typeReferenceParams,
					SERVICE_PARAMETER_VARIABLE, constructorCall);

			target.getBody().insertBegin(nVariable);

			// 1.2. add all parameter values to the container
			List<CtParameter<?>> parameters = target.getParameters();
			CtStatement lastParameterSt = nVariable;
			for (CtParameter<?> parameter : parameters) {
				lastParameterSt = createParameterProcessing(parameter, nVariable, factory, nVariable);
			}

			// 2. add enter service call
			CtExecutableReference<?> enterService = factory.Type().get(ThreadMonitoringController.class)
					.getMethodsByName("enterService").get(0).getReference();
			CtInvocation<?> invocEnterService = factory.createInvocation(
					factory.createVariableRead(threadMonitoringVariable.getReference(), false), enterService,
					factory.createLiteral(probe.getService().getId()),
					factory.createThisAccess(target.getDeclaringType().getReference()),
					factory.createVariableRead(nVariable.getReference(), false));
			lastParameterSt.insertAfter(invocEnterService);

			// 3. wrap all in try finally
			CtTry nTry = factory.createTryWithResource();
			nTry.setBody(factory.createBlock());

			// we need to hardly clone otherwise we cause a concurrent modification
			// exception
			for (CtStatement old : new ArrayList<>(target.getBody().getStatements())) {
				old.delete();
				nTry.getBody().addStatement(old);
			}
			target.setBody(nTry);

			// 4. add exit service in finally part
			nTry.setFinalizer(factory.createBlock());
			CtExecutableReference<?> exitService = factory.Type().get(ThreadMonitoringController.class)
					.getMethodsByName("exitService").get(0).getReference();
			CtInvocation<?> invocExitService = factory.createInvocation(
					factory.createVariableRead(threadMonitoringVariable.getReference(), false), exitService,
					factory.createLiteral(probe.getService().getId()));
			nTry.getFinalizer().addStatement(invocExitService);

			// 5. add getting thread monitoring controller at start
			target.getBody().insertBegin(threadMonitoringVariable);
		}

	}

	private CtInvocation<?> createParameterProcessing(CtParameter<?> parameter, CtLocalVariable<ServiceParameters> var,
			Factory factory, CtStatement position) {
		// atm use always string value of
		CtExecutableReference<?> addReference = factory.Type().get(ServiceParameters.class).getMethodsByName("addValue")
				.get(0).getReference();

		CtInvocation<?> invoc = factory.createInvocation(factory.createVariableRead(var.getReference(), false),
				addReference, factory.createLiteral(parameter.getSimpleName()),
				factory.createVariableRead(parameter.getReference(), false));
		position.insertAfter(invoc);

		return invoc;
	}

}
