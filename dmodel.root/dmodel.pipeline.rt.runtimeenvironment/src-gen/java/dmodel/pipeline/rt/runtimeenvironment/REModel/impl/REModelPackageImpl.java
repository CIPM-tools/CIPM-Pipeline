/**
 */
package dmodel.pipeline.rt.runtimeenvironment.REModel.impl;

import dmodel.pipeline.rt.runtimeenvironment.REModel.HardwareInformation;
import dmodel.pipeline.rt.runtimeenvironment.REModel.REModelFactory;
import dmodel.pipeline.rt.runtimeenvironment.REModel.REModelPackage;
import dmodel.pipeline.rt.runtimeenvironment.REModel.ResourceContainer;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeEnvironmentModel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

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
	private EClass resourceContainerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass hardwareInformationEClass = null;

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
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.REModelPackage#eNS_URI
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
	public EClass getResourceContainer() {
		return resourceContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getResourceContainer_Hostname() {
		return (EAttribute)resourceContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getResourceContainer_HostID() {
		return (EAttribute)resourceContainerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getResourceContainer_Hardware() {
		return (EReference)resourceContainerEClass.getEStructuralFeatures().get(2);
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

		resourceContainerEClass = createEClass(RESOURCE_CONTAINER);
		createEAttribute(resourceContainerEClass, RESOURCE_CONTAINER__HOSTNAME);
		createEAttribute(resourceContainerEClass, RESOURCE_CONTAINER__HOST_ID);
		createEReference(resourceContainerEClass, RESOURCE_CONTAINER__HARDWARE);

		hardwareInformationEClass = createEClass(HARDWARE_INFORMATION);
		createEAttribute(hardwareInformationEClass, HARDWARE_INFORMATION__CORES);
		createEAttribute(hardwareInformationEClass, HARDWARE_INFORMATION__MAIN_MEMORY_SIZE);
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

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(runtimeEnvironmentModelEClass, RuntimeEnvironmentModel.class, "RuntimeEnvironmentModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRuntimeEnvironmentModel_Containers(), this.getResourceContainer(), null, "containers", null, 0, -1, RuntimeEnvironmentModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRuntimeEnvironmentModel_HardwareSpecifications(), this.getHardwareInformation(), null, "hardwareSpecifications", null, 0, -1, RuntimeEnvironmentModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(resourceContainerEClass, ResourceContainer.class, "ResourceContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getResourceContainer_Hostname(), ecorePackage.getEString(), "hostname", null, 0, 1, ResourceContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getResourceContainer_HostID(), ecorePackage.getEString(), "hostID", null, 0, 1, ResourceContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResourceContainer_Hardware(), this.getHardwareInformation(), null, "hardware", null, 0, 1, ResourceContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(hardwareInformationEClass, HardwareInformation.class, "HardwareInformation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getHardwareInformation_Cores(), ecorePackage.getEInt(), "cores", null, 0, 1, HardwareInformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHardwareInformation_MainMemorySize(), ecorePackage.getELong(), "mainMemorySize", null, 0, 1, HardwareInformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //REModelPackageImpl
