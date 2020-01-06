/**
 */
package InstrumentationMetamodel.util;

import InstrumentationMetamodel.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see InstrumentationMetamodel.InstrumentationModelPackage
 * @generated
 */
public class InstrumentationModelAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static InstrumentationModelPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InstrumentationModelAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = InstrumentationModelPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InstrumentationModelSwitch<Adapter> modelSwitch =
		new InstrumentationModelSwitch<Adapter>() {
			@Override
			public Adapter caseInstrumentationModel(InstrumentationModel object) {
				return createInstrumentationModelAdapter();
			}
			@Override
			public Adapter caseServiceInstrumentationPoint(ServiceInstrumentationPoint object) {
				return createServiceInstrumentationPointAdapter();
			}
			@Override
			public Adapter caseInstrumentationPoint(InstrumentationPoint object) {
				return createInstrumentationPointAdapter();
			}
			@Override
			public Adapter caseActionInstrumentationPoint(ActionInstrumentationPoint object) {
				return createActionInstrumentationPointAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link InstrumentationMetamodel.InstrumentationModel <em>Instrumentation Model</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see InstrumentationMetamodel.InstrumentationModel
	 * @generated
	 */
	public Adapter createInstrumentationModelAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link InstrumentationMetamodel.ServiceInstrumentationPoint <em>Service Instrumentation Point</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see InstrumentationMetamodel.ServiceInstrumentationPoint
	 * @generated
	 */
	public Adapter createServiceInstrumentationPointAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link InstrumentationMetamodel.InstrumentationPoint <em>Instrumentation Point</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see InstrumentationMetamodel.InstrumentationPoint
	 * @generated
	 */
	public Adapter createInstrumentationPointAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link InstrumentationMetamodel.ActionInstrumentationPoint <em>Action Instrumentation Point</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see InstrumentationMetamodel.ActionInstrumentationPoint
	 * @generated
	 */
	public Adapter createActionInstrumentationPointAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //InstrumentationModelAdapterFactory
