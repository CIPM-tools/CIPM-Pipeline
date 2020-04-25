/**
 */
package dmodel.base.models.inmodel.InstrumentationMetamodel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage
 * @generated
 */
public interface InstrumentationModelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	InstrumentationModelFactory eINSTANCE = dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Instrumentation Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Instrumentation Model</em>'.
	 * @generated
	 */
	InstrumentationModel createInstrumentationModel();

	/**
	 * Returns a new object of class '<em>Service Instrumentation Point</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Service Instrumentation Point</em>'.
	 * @generated
	 */
	ServiceInstrumentationPoint createServiceInstrumentationPoint();

	/**
	 * Returns a new object of class '<em>Instrumentation Point</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Instrumentation Point</em>'.
	 * @generated
	 */
	InstrumentationPoint createInstrumentationPoint();

	/**
	 * Returns a new object of class '<em>Action Instrumentation Point</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Action Instrumentation Point</em>'.
	 * @generated
	 */
	ActionInstrumentationPoint createActionInstrumentationPoint();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	InstrumentationModelPackage getInstrumentationModelPackage();

} //InstrumentationModelFactory
