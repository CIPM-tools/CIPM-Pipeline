/**
 */
package dmodel.pipeline.rt.runtimeenvironment.REModel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.REModelPackage
 * @generated
 */
public interface REModelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	REModelFactory eINSTANCE = dmodel.pipeline.rt.runtimeenvironment.REModel.impl.REModelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Runtime Environment Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Runtime Environment Model</em>'.
	 * @generated
	 */
	RuntimeEnvironmentModel createRuntimeEnvironmentModel();

	/**
	 * Returns a new object of class '<em>Runtime Resource Container</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Runtime Resource Container</em>'.
	 * @generated
	 */
	RuntimeResourceContainer createRuntimeResourceContainer();

	/**
	 * Returns a new object of class '<em>Hardware Information</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Hardware Information</em>'.
	 * @generated
	 */
	HardwareInformation createHardwareInformation();

	/**
	 * Returns a new object of class '<em>Runtime Resource Container Connection</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Runtime Resource Container Connection</em>'.
	 * @generated
	 */
	RuntimeResourceContainerConnection createRuntimeResourceContainerConnection();

	/**
	 * Returns a new object of class '<em>Connection Specification</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Connection Specification</em>'.
	 * @generated
	 */
	ConnectionSpecification createConnectionSpecification();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	REModelPackage getREModelPackage();

} //REModelFactory
