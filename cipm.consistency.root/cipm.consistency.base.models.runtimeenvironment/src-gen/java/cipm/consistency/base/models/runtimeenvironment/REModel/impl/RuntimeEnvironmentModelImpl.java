/**
 */
package cipm.consistency.base.models.runtimeenvironment.REModel.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.palladiosimulator.pcm.core.entity.impl.EntityImpl;

import cipm.consistency.base.models.runtimeenvironment.REModel.ConnectionSpecification;
import cipm.consistency.base.models.runtimeenvironment.REModel.HardwareInformation;
import cipm.consistency.base.models.runtimeenvironment.REModel.REModelPackage;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Runtime Environment Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link cipm.consistency.base.models.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl#getContainers <em>Containers</em>}</li>
 *   <li>{@link cipm.consistency.base.models.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl#getHardwareSpecifications <em>Hardware Specifications</em>}</li>
 *   <li>{@link cipm.consistency.base.models.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl#getConnectionSpecifications <em>Connection Specifications</em>}</li>
 *   <li>{@link cipm.consistency.base.models.runtimeenvironment.REModel.impl.RuntimeEnvironmentModelImpl#getConnections <em>Connections</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RuntimeEnvironmentModelImpl extends EntityImpl implements RuntimeEnvironmentModel {
	/**
	 * The cached value of the '{@link #getContainers() <em>Containers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContainers()
	 * @generated
	 * @ordered
	 */
	protected EList<RuntimeResourceContainer> containers;

	/**
	 * The cached value of the '{@link #getHardwareSpecifications() <em>Hardware Specifications</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHardwareSpecifications()
	 * @generated
	 * @ordered
	 */
	protected EList<HardwareInformation> hardwareSpecifications;

	/**
	 * The cached value of the '{@link #getConnectionSpecifications() <em>Connection Specifications</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConnectionSpecifications()
	 * @generated
	 * @ordered
	 */
	protected EList<ConnectionSpecification> connectionSpecifications;

	/**
	 * The cached value of the '{@link #getConnections() <em>Connections</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConnections()
	 * @generated
	 * @ordered
	 */
	protected EList<RuntimeResourceContainerConnection> connections;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RuntimeEnvironmentModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return REModelPackage.Literals.RUNTIME_ENVIRONMENT_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<RuntimeResourceContainer> getContainers() {
		if (containers == null) {
			containers = new EObjectContainmentEList<RuntimeResourceContainer>(RuntimeResourceContainer.class, this, REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONTAINERS);
		}
		return containers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<HardwareInformation> getHardwareSpecifications() {
		if (hardwareSpecifications == null) {
			hardwareSpecifications = new EObjectContainmentEList<HardwareInformation>(HardwareInformation.class, this, REModelPackage.RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS);
		}
		return hardwareSpecifications;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ConnectionSpecification> getConnectionSpecifications() {
		if (connectionSpecifications == null) {
			connectionSpecifications = new EObjectContainmentEList<ConnectionSpecification>(ConnectionSpecification.class, this, REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTION_SPECIFICATIONS);
		}
		return connectionSpecifications;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<RuntimeResourceContainerConnection> getConnections() {
		if (connections == null) {
			connections = new EObjectContainmentEList<RuntimeResourceContainerConnection>(RuntimeResourceContainerConnection.class, this, REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTIONS);
		}
		return connections;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONTAINERS:
				return ((InternalEList<?>)getContainers()).basicRemove(otherEnd, msgs);
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS:
				return ((InternalEList<?>)getHardwareSpecifications()).basicRemove(otherEnd, msgs);
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTION_SPECIFICATIONS:
				return ((InternalEList<?>)getConnectionSpecifications()).basicRemove(otherEnd, msgs);
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTIONS:
				return ((InternalEList<?>)getConnections()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONTAINERS:
				return getContainers();
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS:
				return getHardwareSpecifications();
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTION_SPECIFICATIONS:
				return getConnectionSpecifications();
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTIONS:
				return getConnections();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONTAINERS:
				getContainers().clear();
				getContainers().addAll((Collection<? extends RuntimeResourceContainer>)newValue);
				return;
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS:
				getHardwareSpecifications().clear();
				getHardwareSpecifications().addAll((Collection<? extends HardwareInformation>)newValue);
				return;
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTION_SPECIFICATIONS:
				getConnectionSpecifications().clear();
				getConnectionSpecifications().addAll((Collection<? extends ConnectionSpecification>)newValue);
				return;
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTIONS:
				getConnections().clear();
				getConnections().addAll((Collection<? extends RuntimeResourceContainerConnection>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONTAINERS:
				getContainers().clear();
				return;
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS:
				getHardwareSpecifications().clear();
				return;
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTION_SPECIFICATIONS:
				getConnectionSpecifications().clear();
				return;
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTIONS:
				getConnections().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONTAINERS:
				return containers != null && !containers.isEmpty();
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS:
				return hardwareSpecifications != null && !hardwareSpecifications.isEmpty();
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTION_SPECIFICATIONS:
				return connectionSpecifications != null && !connectionSpecifications.isEmpty();
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONNECTIONS:
				return connections != null && !connections.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //RuntimeEnvironmentModelImpl
