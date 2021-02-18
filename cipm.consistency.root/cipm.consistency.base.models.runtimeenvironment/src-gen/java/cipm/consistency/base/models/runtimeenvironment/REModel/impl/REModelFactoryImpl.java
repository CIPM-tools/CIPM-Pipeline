/**
 */
package cipm.consistency.base.models.runtimeenvironment.REModel.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import cipm.consistency.base.models.runtimeenvironment.REModel.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class REModelFactoryImpl extends EFactoryImpl implements REModelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static REModelFactory init() {
		try {
			REModelFactory theREModelFactory = (REModelFactory)EPackage.Registry.INSTANCE.getEFactory(REModelPackage.eNS_URI);
			if (theREModelFactory != null) {
				return theREModelFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new REModelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public REModelFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL: return createRuntimeEnvironmentModel();
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER: return createRuntimeResourceContainer();
			case REModelPackage.HARDWARE_INFORMATION: return createHardwareInformation();
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION: return createRuntimeResourceContainerConnection();
			case REModelPackage.CONNECTION_SPECIFICATION: return createConnectionSpecification();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeEnvironmentModel createRuntimeEnvironmentModel() {
		RuntimeEnvironmentModelImpl runtimeEnvironmentModel = new RuntimeEnvironmentModelImpl();
		return runtimeEnvironmentModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeResourceContainer createRuntimeResourceContainer() {
		RuntimeResourceContainerImpl runtimeResourceContainer = new RuntimeResourceContainerImpl();
		return runtimeResourceContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HardwareInformation createHardwareInformation() {
		HardwareInformationImpl hardwareInformation = new HardwareInformationImpl();
		return hardwareInformation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeResourceContainerConnection createRuntimeResourceContainerConnection() {
		RuntimeResourceContainerConnectionImpl runtimeResourceContainerConnection = new RuntimeResourceContainerConnectionImpl();
		return runtimeResourceContainerConnection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConnectionSpecification createConnectionSpecification() {
		ConnectionSpecificationImpl connectionSpecification = new ConnectionSpecificationImpl();
		return connectionSpecification;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public REModelPackage getREModelPackage() {
		return (REModelPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static REModelPackage getPackage() {
		return REModelPackage.eINSTANCE;
	}

} //REModelFactoryImpl
