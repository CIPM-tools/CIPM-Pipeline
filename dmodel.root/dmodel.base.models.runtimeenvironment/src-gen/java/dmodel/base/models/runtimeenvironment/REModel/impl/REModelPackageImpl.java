/**
 */
package dmodel.base.models.runtimeenvironment.REModel.impl;

import de.uka.ipd.sdq.identifier.IdentifierPackage;

import de.uka.ipd.sdq.probfunction.ProbfunctionPackage;

import de.uka.ipd.sdq.stoex.StoexPackage;

import de.uka.ipd.sdq.units.UnitsPackage;
import dmodel.base.models.runtimeenvironment.REModel.ConnectionSpecification;
import dmodel.base.models.runtimeenvironment.REModel.HardwareInformation;
import dmodel.base.models.runtimeenvironment.REModel.REModelFactory;
import dmodel.base.models.runtimeenvironment.REModel.REModelPackage;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.palladiosimulator.pcm.PcmPackage;

import org.palladiosimulator.pcm.core.entity.EntityPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class REModelPackageImpl extends EPackageImpl implements REModelPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass runtimeEnvironmentModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass runtimeResourceContainerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass hardwareInformationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass runtimeResourceContainerConnectionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass connectionSpecificationEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see dmodel.base.models.runtimeenvironment.REModel.REModelPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private REModelPackageImpl() {
		super(eNS_URI, REModelFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link REModelPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static REModelPackage init() {
		if (isInited) return (REModelPackage)EPackage.Registry.INSTANCE.getEPackage(REModelPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredREModelPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		REModelPackageImpl theREModelPackage = registeredREModelPackage instanceof REModelPackageImpl ? (REModelPackageImpl)registeredREModelPackage : new REModelPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		IdentifierPackage.eINSTANCE.eClass();
		PcmPackage.eINSTANCE.eClass();
		ProbfunctionPackage.eINSTANCE.eClass();
		StoexPackage.eINSTANCE.eClass();
		UnitsPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theREModelPackage.createPackageContents();

		// Initialize created meta-data
		theREModelPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theREModelPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(REModelPackage.eNS_URI, theREModelPackage);
		return theREModelPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRuntimeEnvironmentModel() {
		return runtimeEnvironmentModelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRuntimeEnvironmentModel_Containers() {
		return (EReference)runtimeEnvironmentModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRuntimeEnvironmentModel_HardwareSpecifications() {
		return (EReference)runtimeEnvironmentModelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRuntimeEnvironmentModel_ConnectionSpecifications() {
		return (EReference)runtimeEnvironmentModelEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRuntimeEnvironmentModel_Connections() {
		return (EReference)runtimeEnvironmentModelEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRuntimeResourceContainer() {
		return runtimeResourceContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRuntimeResourceContainer_Hostname() {
		return (EAttribute)runtimeResourceContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRuntimeResourceContainer_HostID() {
		return (EAttribute)runtimeResourceContainerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRuntimeResourceContainer_Hardware() {
		return (EReference)runtimeResourceContainerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getHardwareInformation() {
		return hardwareInformationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getHardwareInformation_Cores() {
		return (EAttribute)hardwareInformationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getHardwareInformation_MainMemorySize() {
		return (EAttribute)hardwareInformationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRuntimeResourceContainerConnection() {
		return runtimeResourceContainerConnectionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRuntimeResourceContainerConnection_ContainerFrom() {
		return (EReference)runtimeResourceContainerConnectionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRuntimeResourceContainerConnection_ContainerTo() {
		return (EReference)runtimeResourceContainerConnectionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRuntimeResourceContainerConnection_ConnectionSpecification() {
		return (EReference)runtimeResourceContainerConnectionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConnectionSpecification() {
		return connectionSpecificationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConnectionSpecification_Bandwidth() {
		return (EAttribute)connectionSpecificationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public REModelFactory getREModelFactory() {
		return (REModelFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		runtimeEnvironmentModelEClass = createEClass(RUNTIME_ENVIRONMENT_MODEL);
		createEReference(runtimeEnvironmentModelEClass, RUNTIME_ENVIRONMENT_MODEL__CONTAINERS);
		createEReference(runtimeEnvironmentModelEClass, RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS);
		createEReference(runtimeEnvironmentModelEClass, RUNTIME_ENVIRONMENT_MODEL__CONNECTION_SPECIFICATIONS);
		createEReference(runtimeEnvironmentModelEClass, RUNTIME_ENVIRONMENT_MODEL__CONNECTIONS);

		runtimeResourceContainerEClass = createEClass(RUNTIME_RESOURCE_CONTAINER);
		createEAttribute(runtimeResourceContainerEClass, RUNTIME_RESOURCE_CONTAINER__HOSTNAME);
		createEAttribute(runtimeResourceContainerEClass, RUNTIME_RESOURCE_CONTAINER__HOST_ID);
		createEReference(runtimeResourceContainerEClass, RUNTIME_RESOURCE_CONTAINER__HARDWARE);

		hardwareInformationEClass = createEClass(HARDWARE_INFORMATION);
		createEAttribute(hardwareInformationEClass, HARDWARE_INFORMATION__CORES);
		createEAttribute(hardwareInformationEClass, HARDWARE_INFORMATION__MAIN_MEMORY_SIZE);

		runtimeResourceContainerConnectionEClass = createEClass(RUNTIME_RESOURCE_CONTAINER_CONNECTION);
		createEReference(runtimeResourceContainerConnectionEClass, RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_FROM);
		createEReference(runtimeResourceContainerConnectionEClass, RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_TO);
		createEReference(runtimeResourceContainerConnectionEClass, RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONNECTION_SPECIFICATION);

		connectionSpecificationEClass = createEClass(CONNECTION_SPECIFICATION);
		createEAttribute(connectionSpecificationEClass, CONNECTION_SPECIFICATION__BANDWIDTH);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		EntityPackage theEntityPackage = (EntityPackage)EPackage.Registry.INSTANCE.getEPackage(EntityPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		runtimeEnvironmentModelEClass.getESuperTypes().add(theEntityPackage.getEntity());
		hardwareInformationEClass.getESuperTypes().add(theEntityPackage.getEntity());
		runtimeResourceContainerConnectionEClass.getESuperTypes().add(theEntityPackage.getEntity());
		connectionSpecificationEClass.getESuperTypes().add(theEntityPackage.getEntity());

		// Initialize classes, features, and operations; add parameters
		initEClass(runtimeEnvironmentModelEClass, RuntimeEnvironmentModel.class, "RuntimeEnvironmentModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRuntimeEnvironmentModel_Containers(), this.getRuntimeResourceContainer(), null, "containers", null, 0, -1, RuntimeEnvironmentModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRuntimeEnvironmentModel_HardwareSpecifications(), this.getHardwareInformation(), null, "hardwareSpecifications", null, 0, -1, RuntimeEnvironmentModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRuntimeEnvironmentModel_ConnectionSpecifications(), this.getConnectionSpecification(), null, "connectionSpecifications", null, 0, -1, RuntimeEnvironmentModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRuntimeEnvironmentModel_Connections(), this.getRuntimeResourceContainerConnection(), null, "connections", null, 0, -1, RuntimeEnvironmentModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(runtimeResourceContainerEClass, RuntimeResourceContainer.class, "RuntimeResourceContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRuntimeResourceContainer_Hostname(), ecorePackage.getEString(), "hostname", null, 0, 1, RuntimeResourceContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRuntimeResourceContainer_HostID(), ecorePackage.getEString(), "hostID", null, 0, 1, RuntimeResourceContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRuntimeResourceContainer_Hardware(), this.getHardwareInformation(), null, "hardware", null, 0, 1, RuntimeResourceContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(hardwareInformationEClass, HardwareInformation.class, "HardwareInformation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getHardwareInformation_Cores(), ecorePackage.getEInt(), "cores", null, 0, 1, HardwareInformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHardwareInformation_MainMemorySize(), ecorePackage.getELong(), "mainMemorySize", null, 0, 1, HardwareInformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(runtimeResourceContainerConnectionEClass, RuntimeResourceContainerConnection.class, "RuntimeResourceContainerConnection", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRuntimeResourceContainerConnection_ContainerFrom(), this.getRuntimeResourceContainer(), null, "containerFrom", null, 1, 1, RuntimeResourceContainerConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRuntimeResourceContainerConnection_ContainerTo(), this.getRuntimeResourceContainer(), null, "containerTo", null, 0, 1, RuntimeResourceContainerConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRuntimeResourceContainerConnection_ConnectionSpecification(), this.getConnectionSpecification(), null, "connectionSpecification", null, 0, 1, RuntimeResourceContainerConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(connectionSpecificationEClass, ConnectionSpecification.class, "ConnectionSpecification", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getConnectionSpecification_Bandwidth(), ecorePackage.getEDouble(), "bandwidth", null, 0, 1, ConnectionSpecification.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //REModelPackageImpl
