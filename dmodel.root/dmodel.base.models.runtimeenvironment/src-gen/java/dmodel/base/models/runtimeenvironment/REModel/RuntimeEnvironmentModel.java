/**
 */
package dmodel.base.models.runtimeenvironment.REModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.palladiosimulator.pcm.core.entity.Entity;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Runtime Environment Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getContainers <em>Containers</em>}</li>
 *   <li>{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getHardwareSpecifications <em>Hardware Specifications</em>}</li>
 *   <li>{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getConnectionSpecifications <em>Connection Specifications</em>}</li>
 *   <li>{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getConnections <em>Connections</em>}</li>
 * </ul>
 *
 * @see dmodel.base.models.runtimeenvironment.REModel.REModelPackage#getRuntimeEnvironmentModel()
 * @model
 * @generated
 */
public interface RuntimeEnvironmentModel extends EObject, Entity {
	/**
	 * Returns the value of the '<em><b>Containers</b></em>' containment reference list.
	 * The list contents are of type {@link dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Containers</em>' containment reference list.
	 * @see dmodel.base.models.runtimeenvironment.REModel.REModelPackage#getRuntimeEnvironmentModel_Containers()
	 * @model containment="true"
	 * @generated
	 */
	EList<RuntimeResourceContainer> getContainers();

	/**
	 * Returns the value of the '<em><b>Hardware Specifications</b></em>' containment reference list.
	 * The list contents are of type {@link dmodel.base.models.runtimeenvironment.REModel.HardwareInformation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hardware Specifications</em>' containment reference list.
	 * @see dmodel.base.models.runtimeenvironment.REModel.REModelPackage#getRuntimeEnvironmentModel_HardwareSpecifications()
	 * @model containment="true"
	 * @generated
	 */
	EList<HardwareInformation> getHardwareSpecifications();

	/**
	 * Returns the value of the '<em><b>Connection Specifications</b></em>' containment reference list.
	 * The list contents are of type {@link dmodel.base.models.runtimeenvironment.REModel.ConnectionSpecification}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Connection Specifications</em>' containment reference list.
	 * @see dmodel.base.models.runtimeenvironment.REModel.REModelPackage#getRuntimeEnvironmentModel_ConnectionSpecifications()
	 * @model containment="true"
	 * @generated
	 */
	EList<ConnectionSpecification> getConnectionSpecifications();

	/**
	 * Returns the value of the '<em><b>Connections</b></em>' containment reference list.
	 * The list contents are of type {@link dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Connections</em>' containment reference list.
	 * @see dmodel.base.models.runtimeenvironment.REModel.REModelPackage#getRuntimeEnvironmentModel_Connections()
	 * @model containment="true"
	 * @generated
	 */
	EList<RuntimeResourceContainerConnection> getConnections();

} // RuntimeEnvironmentModel
