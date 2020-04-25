/**
 */
package dmodel.base.models.runtimeenvironment.REModel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.palladiosimulator.pcm.core.entity.EntityPackage;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each operation of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see dmodel.base.models.runtimeenvironment.REModel.REModelFactory
 * @model kind="package"
 * @generated
 */
public interface REModelPackage extends EPackage {
	/**
	 * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNAME = "REModel";

	/**
	 * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_URI = "http://www.dmodel.com/RuntimeEnvironmentModel";

	/**
	 * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_PREFIX = "rem";

	/**
	 * The singleton instance of the package. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	REModelPackage eINSTANCE = dmodel.base.models.runtimeenvironment.REModel.impl.REModelPackageImpl.init();

	/**
	 * The meta object id for the
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl
	 * <em>Runtime Environment Model</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl
	 * @see dmodel.base.models.runtimeenvironment.REModel.impl.REModelPackageImpl#getRuntimeEnvironmentModel()
	 * @generated
	 */
	int RUNTIME_ENVIRONMENT_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL__ID = EntityPackage.ENTITY__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL__ENTITY_NAME = EntityPackage.ENTITY__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Containers</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL__CONTAINERS = EntityPackage.ENTITY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Hardware Specifications</b></em>' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS = EntityPackage.ENTITY_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Connection Specifications</b></em>'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL__CONNECTION_SPECIFICATIONS = EntityPackage.ENTITY_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Connections</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL__CONNECTIONS = EntityPackage.ENTITY_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Runtime Environment Model</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL_FEATURE_COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 4;

	/**
	 * The number of operations of the '<em>Runtime Environment Model</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_ENVIRONMENT_MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeResourceContainerImpl
	 * <em>Runtime Resource Container</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeResourceContainerImpl
	 * @see dmodel.base.models.runtimeenvironment.REModel.impl.REModelPackageImpl#getRuntimeResourceContainer()
	 * @generated
	 */
	int RUNTIME_RESOURCE_CONTAINER = 1;

	/**
	 * The feature id for the '<em><b>Hostname</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER__HOSTNAME = 0;

	/**
	 * The feature id for the '<em><b>Host ID</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER__HOST_ID = 1;

	/**
	 * The feature id for the '<em><b>Hardware</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER__HARDWARE = 2;

	/**
	 * The number of structural features of the '<em>Runtime Resource
	 * Container</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Runtime Resource Container</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.impl.HardwareInformationImpl
	 * <em>Hardware Information</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see dmodel.base.models.runtimeenvironment.REModel.impl.HardwareInformationImpl
	 * @see dmodel.base.models.runtimeenvironment.REModel.impl.REModelPackageImpl#getHardwareInformation()
	 * @generated
	 */
	int HARDWARE_INFORMATION = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int HARDWARE_INFORMATION__ID = EntityPackage.ENTITY__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int HARDWARE_INFORMATION__ENTITY_NAME = EntityPackage.ENTITY__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Cores</b></em>' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int HARDWARE_INFORMATION__CORES = EntityPackage.ENTITY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Main Memory Size</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int HARDWARE_INFORMATION__MAIN_MEMORY_SIZE = EntityPackage.ENTITY_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Hardware Information</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int HARDWARE_INFORMATION_FEATURE_COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Hardware Information</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int HARDWARE_INFORMATION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeResourceContainerConnectionImpl
	 * <em>Runtime Resource Container Connection</em>}' class. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @see dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeResourceContainerConnectionImpl
	 * @see dmodel.base.models.runtimeenvironment.REModel.impl.REModelPackageImpl#getRuntimeResourceContainerConnection()
	 * @generated
	 */
	int RUNTIME_RESOURCE_CONTAINER_CONNECTION = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER_CONNECTION__ID = EntityPackage.ENTITY__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER_CONNECTION__ENTITY_NAME = EntityPackage.ENTITY__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Container From</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_FROM = EntityPackage.ENTITY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Container To</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_TO = EntityPackage.ENTITY_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Connection Specification</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONNECTION_SPECIFICATION = EntityPackage.ENTITY_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Runtime Resource Container
	 * Connection</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER_CONNECTION_FEATURE_COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Runtime Resource Container
	 * Connection</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int RUNTIME_RESOURCE_CONTAINER_CONNECTION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.impl.ConnectionSpecificationImpl
	 * <em>Connection Specification</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see dmodel.base.models.runtimeenvironment.REModel.impl.ConnectionSpecificationImpl
	 * @see dmodel.base.models.runtimeenvironment.REModel.impl.REModelPackageImpl#getConnectionSpecification()
	 * @generated
	 */
	int CONNECTION_SPECIFICATION = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONNECTION_SPECIFICATION__ID = EntityPackage.ENTITY__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONNECTION_SPECIFICATION__ENTITY_NAME = EntityPackage.ENTITY__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Bandwidth</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONNECTION_SPECIFICATION__BANDWIDTH = EntityPackage.ENTITY_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Connection Specification</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONNECTION_SPECIFICATION_FEATURE_COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Connection Specification</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONNECTION_SPECIFICATION_OPERATION_COUNT = 0;

	/**
	 * Returns the meta object for class
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel
	 * <em>Runtime Environment Model</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Runtime Environment Model</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel
	 * @generated
	 */
	EClass getRuntimeEnvironmentModel();

	/**
	 * Returns the meta object for the containment reference list
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getContainers
	 * <em>Containers</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list
	 *         '<em>Containers</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getContainers()
	 * @see #getRuntimeEnvironmentModel()
	 * @generated
	 */
	EReference getRuntimeEnvironmentModel_Containers();

	/**
	 * Returns the meta object for the containment reference list
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getHardwareSpecifications
	 * <em>Hardware Specifications</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @return the meta object for the containment reference list '<em>Hardware
	 *         Specifications</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getHardwareSpecifications()
	 * @see #getRuntimeEnvironmentModel()
	 * @generated
	 */
	EReference getRuntimeEnvironmentModel_HardwareSpecifications();

	/**
	 * Returns the meta object for the containment reference list
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getConnectionSpecifications
	 * <em>Connection Specifications</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Connection
	 *         Specifications</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getConnectionSpecifications()
	 * @see #getRuntimeEnvironmentModel()
	 * @generated
	 */
	EReference getRuntimeEnvironmentModel_ConnectionSpecifications();

	/**
	 * Returns the meta object for the containment reference list
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getConnections
	 * <em>Connections</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list
	 *         '<em>Connections</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel#getConnections()
	 * @see #getRuntimeEnvironmentModel()
	 * @generated
	 */
	EReference getRuntimeEnvironmentModel_Connections();

	/**
	 * Returns the meta object for class
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer
	 * <em>Runtime Resource Container</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Runtime Resource Container</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer
	 * @generated
	 */
	EClass getRuntimeResourceContainer();

	/**
	 * Returns the meta object for the attribute
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHostname
	 * <em>Hostname</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Hostname</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHostname()
	 * @see #getRuntimeResourceContainer()
	 * @generated
	 */
	EAttribute getRuntimeResourceContainer_Hostname();

	/**
	 * Returns the meta object for the attribute
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHostID
	 * <em>Host ID</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Host ID</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHostID()
	 * @see #getRuntimeResourceContainer()
	 * @generated
	 */
	EAttribute getRuntimeResourceContainer_HostID();

	/**
	 * Returns the meta object for the reference
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHardware
	 * <em>Hardware</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Hardware</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer#getHardware()
	 * @see #getRuntimeResourceContainer()
	 * @generated
	 */
	EReference getRuntimeResourceContainer_Hardware();

	/**
	 * Returns the meta object for class
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.HardwareInformation
	 * <em>Hardware Information</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @return the meta object for class '<em>Hardware Information</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.HardwareInformation
	 * @generated
	 */
	EClass getHardwareInformation();

	/**
	 * Returns the meta object for the attribute
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.HardwareInformation#getCores
	 * <em>Cores</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Cores</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.HardwareInformation#getCores()
	 * @see #getHardwareInformation()
	 * @generated
	 */
	EAttribute getHardwareInformation_Cores();

	/**
	 * Returns the meta object for the attribute
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.HardwareInformation#getMainMemorySize
	 * <em>Main Memory Size</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Main Memory Size</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.HardwareInformation#getMainMemorySize()
	 * @see #getHardwareInformation()
	 * @generated
	 */
	EAttribute getHardwareInformation_MainMemorySize();

	/**
	 * Returns the meta object for class
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection
	 * <em>Runtime Resource Container Connection</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Runtime Resource Container
	 *         Connection</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection
	 * @generated
	 */
	EClass getRuntimeResourceContainerConnection();

	/**
	 * Returns the meta object for the reference
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getContainerFrom
	 * <em>Container From</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Container From</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getContainerFrom()
	 * @see #getRuntimeResourceContainerConnection()
	 * @generated
	 */
	EReference getRuntimeResourceContainerConnection_ContainerFrom();

	/**
	 * Returns the meta object for the reference
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getContainerTo
	 * <em>Container To</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Container To</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getContainerTo()
	 * @see #getRuntimeResourceContainerConnection()
	 * @generated
	 */
	EReference getRuntimeResourceContainerConnection_ContainerTo();

	/**
	 * Returns the meta object for the reference
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getConnectionSpecification
	 * <em>Connection Specification</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Connection
	 *         Specification</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getConnectionSpecification()
	 * @see #getRuntimeResourceContainerConnection()
	 * @generated
	 */
	EReference getRuntimeResourceContainerConnection_ConnectionSpecification();

	/**
	 * Returns the meta object for class
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.ConnectionSpecification
	 * <em>Connection Specification</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Connection Specification</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.ConnectionSpecification
	 * @generated
	 */
	EClass getConnectionSpecification();

	/**
	 * Returns the meta object for the attribute
	 * '{@link dmodel.base.models.runtimeenvironment.REModel.ConnectionSpecification#getBandwidth
	 * <em>Bandwidth</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Bandwidth</em>'.
	 * @see dmodel.base.models.runtimeenvironment.REModel.ConnectionSpecification#getBandwidth()
	 * @see #getConnectionSpecification()
	 * @generated
	 */
	EAttribute getConnectionSpecification_Bandwidth();

	/**
	 * Returns the factory that creates the instances of the model. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	REModelFactory getREModelFactory();

	/**
	 * <!-- begin-user-doc --> Defines literals for the meta objects that represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each operation of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the
		 * '{@link dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl
		 * <em>Runtime Environment Model</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl
		 * @see dmodel.base.models.runtimeenvironment.REModel.impl.REModelPackageImpl#getRuntimeEnvironmentModel()
		 * @generated
		 */
		EClass RUNTIME_ENVIRONMENT_MODEL = eINSTANCE.getRuntimeEnvironmentModel();

		/**
		 * The meta object literal for the '<em><b>Containers</b></em>' containment
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference RUNTIME_ENVIRONMENT_MODEL__CONTAINERS = eINSTANCE.getRuntimeEnvironmentModel_Containers();

		/**
		 * The meta object literal for the '<em><b>Hardware Specifications</b></em>'
		 * containment reference list feature. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @generated
		 */
		EReference RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS = eINSTANCE
				.getRuntimeEnvironmentModel_HardwareSpecifications();

		/**
		 * The meta object literal for the '<em><b>Connection Specifications</b></em>'
		 * containment reference list feature. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @generated
		 */
		EReference RUNTIME_ENVIRONMENT_MODEL__CONNECTION_SPECIFICATIONS = eINSTANCE
				.getRuntimeEnvironmentModel_ConnectionSpecifications();

		/**
		 * The meta object literal for the '<em><b>Connections</b></em>' containment
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference RUNTIME_ENVIRONMENT_MODEL__CONNECTIONS = eINSTANCE.getRuntimeEnvironmentModel_Connections();

		/**
		 * The meta object literal for the
		 * '{@link dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeResourceContainerImpl
		 * <em>Runtime Resource Container</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeResourceContainerImpl
		 * @see dmodel.base.models.runtimeenvironment.REModel.impl.REModelPackageImpl#getRuntimeResourceContainer()
		 * @generated
		 */
		EClass RUNTIME_RESOURCE_CONTAINER = eINSTANCE.getRuntimeResourceContainer();

		/**
		 * The meta object literal for the '<em><b>Hostname</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute RUNTIME_RESOURCE_CONTAINER__HOSTNAME = eINSTANCE.getRuntimeResourceContainer_Hostname();

		/**
		 * The meta object literal for the '<em><b>Host ID</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute RUNTIME_RESOURCE_CONTAINER__HOST_ID = eINSTANCE.getRuntimeResourceContainer_HostID();

		/**
		 * The meta object literal for the '<em><b>Hardware</b></em>' reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference RUNTIME_RESOURCE_CONTAINER__HARDWARE = eINSTANCE.getRuntimeResourceContainer_Hardware();

		/**
		 * The meta object literal for the
		 * '{@link dmodel.base.models.runtimeenvironment.REModel.impl.HardwareInformationImpl
		 * <em>Hardware Information</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see dmodel.base.models.runtimeenvironment.REModel.impl.HardwareInformationImpl
		 * @see dmodel.base.models.runtimeenvironment.REModel.impl.REModelPackageImpl#getHardwareInformation()
		 * @generated
		 */
		EClass HARDWARE_INFORMATION = eINSTANCE.getHardwareInformation();

		/**
		 * The meta object literal for the '<em><b>Cores</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute HARDWARE_INFORMATION__CORES = eINSTANCE.getHardwareInformation_Cores();

		/**
		 * The meta object literal for the '<em><b>Main Memory Size</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute HARDWARE_INFORMATION__MAIN_MEMORY_SIZE = eINSTANCE.getHardwareInformation_MainMemorySize();

		/**
		 * The meta object literal for the
		 * '{@link dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeResourceContainerConnectionImpl
		 * <em>Runtime Resource Container Connection</em>}' class. <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * 
		 * @see dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeResourceContainerConnectionImpl
		 * @see dmodel.base.models.runtimeenvironment.REModel.impl.REModelPackageImpl#getRuntimeResourceContainerConnection()
		 * @generated
		 */
		EClass RUNTIME_RESOURCE_CONTAINER_CONNECTION = eINSTANCE.getRuntimeResourceContainerConnection();

		/**
		 * The meta object literal for the '<em><b>Container From</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_FROM = eINSTANCE
				.getRuntimeResourceContainerConnection_ContainerFrom();

		/**
		 * The meta object literal for the '<em><b>Container To</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_TO = eINSTANCE
				.getRuntimeResourceContainerConnection_ContainerTo();

		/**
		 * The meta object literal for the '<em><b>Connection Specification</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONNECTION_SPECIFICATION = eINSTANCE
				.getRuntimeResourceContainerConnection_ConnectionSpecification();

		/**
		 * The meta object literal for the
		 * '{@link dmodel.base.models.runtimeenvironment.REModel.impl.ConnectionSpecificationImpl
		 * <em>Connection Specification</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see dmodel.base.models.runtimeenvironment.REModel.impl.ConnectionSpecificationImpl
		 * @see dmodel.base.models.runtimeenvironment.REModel.impl.REModelPackageImpl#getConnectionSpecification()
		 * @generated
		 */
		EClass CONNECTION_SPECIFICATION = eINSTANCE.getConnectionSpecification();

		/**
		 * The meta object literal for the '<em><b>Bandwidth</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute CONNECTION_SPECIFICATION__BANDWIDTH = eINSTANCE.getConnectionSpecification_Bandwidth();

	}

} // REModelPackage
