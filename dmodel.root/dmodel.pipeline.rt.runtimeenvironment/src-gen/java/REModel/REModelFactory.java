/**
 */
package REModel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see REModel.REModelPackage
 * @generated
 */
public interface REModelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	REModelFactory eINSTANCE = REModel.impl.REModelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Runtime Environment Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Runtime Environment Model</em>'.
	 * @generated
	 */
	RuntimeEnvironmentModel createRuntimeEnvironmentModel();

	/**
	 * Returns a new object of class '<em>Resource Container</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Resource Container</em>'.
	 * @generated
	 */
	ResourceContainer createResourceContainer();

	/**
	 * Returns a new object of class '<em>Hardware Information</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Hardware Information</em>'.
	 * @generated
	 */
	HardwareInformation createHardwareInformation();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	REModelPackage getREModelPackage();

} //REModelFactory
