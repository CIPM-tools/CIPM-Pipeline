/**
 */
package dmodel.pipeline.rt.runtimeenvironment.REModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Runtime Environment Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel#getContainers <em>Containers</em>}</li>
 *   <li>{@link dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel#getHardwareSpecifications <em>Hardware Specifications</em>}</li>
 * </ul>
 *
 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.REModelPackage#getRuntimeEnvironmentModel()
 * @model
 * @generated
 */
public interface RuntimeEnvironmentModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Containers</b></em>' containment reference list.
	 * The list contents are of type {@link dmodel.pipeline.rt.runtimeenvironment.REModel.ResourceContainer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Containers</em>' containment reference list.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.REModelPackage#getRuntimeEnvironmentModel_Containers()
	 * @model containment="true"
	 * @generated
	 */
	EList<ResourceContainer> getContainers();

	/**
	 * Returns the value of the '<em><b>Hardware Specifications</b></em>' containment reference list.
	 * The list contents are of type {@link dmodel.pipeline.rt.runtimeenvironment.REModel.HardwareInformation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hardware Specifications</em>' containment reference list.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.REModelPackage#getRuntimeEnvironmentModel_HardwareSpecifications()
	 * @model containment="true"
	 * @generated
	 */
	EList<HardwareInformation> getHardwareSpecifications();

} // RuntimeEnvironmentModel
