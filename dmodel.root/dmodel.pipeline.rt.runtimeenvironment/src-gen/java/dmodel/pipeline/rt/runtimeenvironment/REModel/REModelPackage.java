/**
 */
package dmodel.pipeline.rt.runtimeenvironment.REModel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.REModelFactory
 * @model kind="package"
 * @generated
 */
public interface REModelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "REModel";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.dmodel.com/RuntimeEnvironmentModel";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "rem";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	REModelPackage eINSTANCE = dmodel.pipeline.rt.runtimeenvironment.REModel.impl.REModelPackageImpl.init();

	/**
	 * The meta object id for the '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl <em>Runtime Environment Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.REModelPackageImpl#getRuntimeEnvironmentModel()
	 * @generated
	 */
	int RUNTIME_ENVIRONMENT_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Containers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL__CONTAINERS = 0;

	/**
	 * The feature id for the '<em><b>Hardware Specifications</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS = 1;

	/**
	 * The number of structural features of the '<em>Runtime Environment Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Runtime Environment Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.impl.ResourceContainerImpl <em>Resource Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.ResourceContainerImpl
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.REModelPackageImpl#getResourceContainer()
	 * @generated
	 */
	int RESOURCE_CONTAINER = 1;

	/**
	 * The feature id for the '<em><b>Hostname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_CONTAINER__HOSTNAME = 0;

	/**
	 * The feature id for the '<em><b>Host ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_CONTAINER__HOST_ID = 1;

	/**
	 * The feature id for the '<em><b>Hardware</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_CONTAINER__HARDWARE = 2;

	/**
	 * The number of structural features of the '<em>Resource Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_CONTAINER_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Resource Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_CONTAINER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.impl.HardwareInformationImpl <em>Hardware Information</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.HardwareInformationImpl
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.REModelPackageImpl#getHardwareInformation()
	 * @generated
	 */
	int HARDWARE_INFORMATION = 2;

	/**
	 * The feature id for the '<em><b>Cores</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HARDWARE_INFORMATION__CORES = 0;

	/**
	 * The feature id for the '<em><b>Main Memory Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HARDWARE_INFORMATION__MAIN_MEMORY_SIZE = 1;

	/**
	 * The number of structural features of the '<em>Hardware Information</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HARDWARE_INFORMATION_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Hardware Information</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HARDWARE_INFORMATION_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel <em>Runtime Environment Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Runtime Environment Model</em>'.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel
	 * @generated
	 */
	EClass getRuntimeEnvironmentModel();

	/**
	 * Returns the meta object for the containment reference list '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel#getContainers <em>Containers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Containers</em>'.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel#getContainers()
	 * @see #getRuntimeEnvironmentModel()
	 * @generated
	 */
	EReference getRuntimeEnvironmentModel_Containers();

	/**
	 * Returns the meta object for the containment reference list '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel#getHardwareSpecifications <em>Hardware Specifications</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Hardware Specifications</em>'.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel#getHardwareSpecifications()
	 * @see #getRuntimeEnvironmentModel()
	 * @generated
	 */
	EReference getRuntimeEnvironmentModel_HardwareSpecifications();

	/**
	 * Returns the meta object for class '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.ResourceContainer <em>Resource Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Resource Container</em>'.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.ResourceContainer
	 * @generated
	 */
	EClass getResourceContainer();

	/**
	 * Returns the meta object for the attribute '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.ResourceContainer#getHostname <em>Hostname</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Hostname</em>'.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.ResourceContainer#getHostname()
	 * @see #getResourceContainer()
	 * @generated
	 */
	EAttribute getResourceContainer_Hostname();

	/**
	 * Returns the meta object for the attribute '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.ResourceContainer#getHostID <em>Host ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Host ID</em>'.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.ResourceContainer#getHostID()
	 * @see #getResourceContainer()
	 * @generated
	 */
	EAttribute getResourceContainer_HostID();

	/**
	 * Returns the meta object for the reference '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.ResourceContainer#getHardware <em>Hardware</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Hardware</em>'.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.ResourceContainer#getHardware()
	 * @see #getResourceContainer()
	 * @generated
	 */
	EReference getResourceContainer_Hardware();

	/**
	 * Returns the meta object for class '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.HardwareInformation <em>Hardware Information</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Hardware Information</em>'.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.HardwareInformation
	 * @generated
	 */
	EClass getHardwareInformation();

	/**
	 * Returns the meta object for the attribute '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.HardwareInformation#getCores <em>Cores</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Cores</em>'.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.HardwareInformation#getCores()
	 * @see #getHardwareInformation()
	 * @generated
	 */
	EAttribute getHardwareInformation_Cores();

	/**
	 * Returns the meta object for the attribute '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.HardwareInformation#getMainMemorySize <em>Main Memory Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Main Memory Size</em>'.
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.HardwareInformation#getMainMemorySize()
	 * @see #getHardwareInformation()
	 * @generated
	 */
	EAttribute getHardwareInformation_MainMemorySize();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	REModelFactory getREModelFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl <em>Runtime Environment Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl
		 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.REModelPackageImpl#getRuntimeEnvironmentModel()
		 * @generated
		 */
		EClass RUNTIME_ENVIRONMENT_MODEL = eINSTANCE.getRuntimeEnvironmentModel();

		/**
		 * The meta object literal for the '<em><b>Containers</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RUNTIME_ENVIRONMENT_MODEL__CONTAINERS = eINSTANCE.getRuntimeEnvironmentModel_Containers();

		/**
		 * The meta object literal for the '<em><b>Hardware Specifications</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS = eINSTANCE.getRuntimeEnvironmentModel_HardwareSpecifications();

		/**
		 * The meta object literal for the '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.impl.ResourceContainerImpl <em>Resource Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.ResourceContainerImpl
		 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.REModelPackageImpl#getResourceContainer()
		 * @generated
		 */
		EClass RESOURCE_CONTAINER = eINSTANCE.getResourceContainer();

		/**
		 * The meta object literal for the '<em><b>Hostname</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESOURCE_CONTAINER__HOSTNAME = eINSTANCE.getResourceContainer_Hostname();

		/**
		 * The meta object literal for the '<em><b>Host ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESOURCE_CONTAINER__HOST_ID = eINSTANCE.getResourceContainer_HostID();

		/**
		 * The meta object literal for the '<em><b>Hardware</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_CONTAINER__HARDWARE = eINSTANCE.getResourceContainer_Hardware();

		/**
		 * The meta object literal for the '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.impl.HardwareInformationImpl <em>Hardware Information</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.HardwareInformationImpl
		 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.impl.REModelPackageImpl#getHardwareInformation()
		 * @generated
		 */
		EClass HARDWARE_INFORMATION = eINSTANCE.getHardwareInformation();

		/**
		 * The meta object literal for the '<em><b>Cores</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HARDWARE_INFORMATION__CORES = eINSTANCE.getHardwareInformation_Cores();

		/**
		 * The meta object literal for the '<em><b>Main Memory Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HARDWARE_INFORMATION__MAIN_MEMORY_SIZE = eINSTANCE.getHardwareInformation_MainMemorySize();

	}

} //REModelPackage
