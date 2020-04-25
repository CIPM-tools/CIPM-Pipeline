/**
 */
package dmodel.base.models.runtimeenvironment.REModel;

import org.eclipse.emf.ecore.EObject;

import org.palladiosimulator.pcm.core.entity.Entity;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Hardware Information</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dmodel.base.models.runtimeenvironment.REModel.HardwareInformation#getCores <em>Cores</em>}</li>
 *   <li>{@link dmodel.base.models.runtimeenvironment.REModel.HardwareInformation#getMainMemorySize <em>Main Memory Size</em>}</li>
 * </ul>
 *
 * @see dmodel.base.models.runtimeenvironment.REModel.REModelPackage#getHardwareInformation()
 * @model
 * @generated
 */
public interface HardwareInformation extends EObject, Entity {
	/**
	 * Returns the value of the '<em><b>Cores</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cores</em>' attribute.
	 * @see #setCores(int)
	 * @see dmodel.base.models.runtimeenvironment.REModel.REModelPackage#getHardwareInformation_Cores()
	 * @model
	 * @generated
	 */
	int getCores();

	/**
	 * Sets the value of the '{@link dmodel.base.models.runtimeenvironment.REModel.HardwareInformation#getCores <em>Cores</em>}' attribute.
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
	 * @see dmodel.base.models.runtimeenvironment.REModel.REModelPackage#getHardwareInformation_MainMemorySize()
	 * @model
	 * @generated
	 */
	long getMainMemorySize();

	/**
	 * Sets the value of the '{@link dmodel.base.models.runtimeenvironment.REModel.HardwareInformation#getMainMemorySize <em>Main Memory Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Main Memory Size</em>' attribute.
	 * @see #getMainMemorySize()
	 * @generated
	 */
	void setMainMemorySize(long value);

} // HardwareInformation
