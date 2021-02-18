/**
 */
package cipm.consistency.base.models.runtimeenvironment.REModel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Runtime Resource Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHostname <em>Hostname</em>}</li>
 *   <li>{@link cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHostID <em>Host ID</em>}</li>
 *   <li>{@link cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHardware <em>Hardware</em>}</li>
 * </ul>
 *
 * @see cipm.consistency.base.models.runtimeenvironment.REModel.REModelPackage#getRuntimeResourceContainer()
 * @model
 * @generated
 */
public interface RuntimeResourceContainer extends EObject {
	/**
	 * Returns the value of the '<em><b>Hostname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hostname</em>' attribute.
	 * @see #setHostname(String)
	 * @see cipm.consistency.base.models.runtimeenvironment.REModel.REModelPackage#getRuntimeResourceContainer_Hostname()
	 * @model
	 * @generated
	 */
	String getHostname();

	/**
	 * Sets the value of the '{@link cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHostname <em>Hostname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Hostname</em>' attribute.
	 * @see #getHostname()
	 * @generated
	 */
	void setHostname(String value);

	/**
	 * Returns the value of the '<em><b>Host ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Host ID</em>' attribute.
	 * @see #setHostID(String)
	 * @see cipm.consistency.base.models.runtimeenvironment.REModel.REModelPackage#getRuntimeResourceContainer_HostID()
	 * @model id="true"
	 * @generated
	 */
	String getHostID();

	/**
	 * Sets the value of the '{@link cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHostID <em>Host ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Host ID</em>' attribute.
	 * @see #getHostID()
	 * @generated
	 */
	void setHostID(String value);

	/**
	 * Returns the value of the '<em><b>Hardware</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hardware</em>' reference.
	 * @see #setHardware(HardwareInformation)
	 * @see cipm.consistency.base.models.runtimeenvironment.REModel.REModelPackage#getRuntimeResourceContainer_Hardware()
	 * @model
	 * @generated
	 */
	HardwareInformation getHardware();

	/**
	 * Sets the value of the '{@link cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHardware <em>Hardware</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Hardware</em>' reference.
	 * @see #getHardware()
	 * @generated
	 */
	void setHardware(HardwareInformation value);

} // RuntimeResourceContainer
