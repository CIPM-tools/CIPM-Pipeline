/**
 */
package REModel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Hardware Information</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link REModel.HardwareInformation#getCores <em>Cores</em>}</li>
 *   <li>{@link REModel.HardwareInformation#getMainMemorySize <em>Main Memory Size</em>}</li>
 * </ul>
 *
 * @see REModel.REModelPackage#getHardwareInformation()
 * @model
 * @generated
 */
public interface HardwareInformation extends EObject {
	/**
	 * Returns the value of the '<em><b>Cores</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cores</em>' attribute.
	 * @see #setCores(int)
	 * @see REModel.REModelPackage#getHardwareInformation_Cores()
	 * @model
	 * @generated
	 */
	int getCores();

	/**
	 * Sets the value of the '{@link REModel.HardwareInformation#getCores <em>Cores</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Cores</em>' attribute.
	 * @see #getCores()
	 * @generated
	 */
	void setCores(int value);

	/**
	 * Returns the value of the '<em><b>Main Memory Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Main Memory Size</em>' attribute.
	 * @see #setMainMemorySize(long)
	 * @see REModel.REModelPackage#getHardwareInformation_MainMemorySize()
	 * @model
	 * @generated
	 */
	long getMainMemorySize();

	/**
	 * Sets the value of the '{@link REModel.HardwareInformation#getMainMemorySize <em>Main Memory Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Main Memory Size</em>' attribute.
	 * @see #getMainMemorySize()
	 * @generated
	 */
	void setMainMemorySize(long value);

} // HardwareInformation
