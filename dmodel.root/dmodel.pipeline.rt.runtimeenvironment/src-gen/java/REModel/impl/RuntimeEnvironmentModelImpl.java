/**
 */
package REModel.impl;

import REModel.HardwareInformation;
import REModel.REModelPackage;
import REModel.ResourceContainer;
import REModel.RuntimeEnvironmentModel;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Runtime Environment Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link REModel.impl.RuntimeEnvironmentModelImpl#getContainers <em>Containers</em>}</li>
 *   <li>{@link REModel.impl.RuntimeEnvironmentModelImpl#getHardwareSpecifications <em>Hardware Specifications</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RuntimeEnvironmentModelImpl extends MinimalEObjectImpl.Container implements RuntimeEnvironmentModel {
	/**
	 * The cached value of the '{@link #getContainers() <em>Containers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContainers()
	 * @generated
	 * @ordered
	 */
	protected EList<ResourceContainer> containers;

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
	public EList<ResourceContainer> getContainers() {
		if (containers == null) {
			containers = new EObjectContainmentEList<ResourceContainer>(ResourceContainer.class, this, REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONTAINERS);
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
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__CONTAINERS:
				return ((InternalEList<?>)getContainers()).basicRemove(otherEnd, msgs);
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS:
				return ((InternalEList<?>)getHardwareSpecifications()).basicRemove(otherEnd, msgs);
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
				getContainers().addAll((Collection<? extends ResourceContainer>)newValue);
				return;
			case REModelPackage.RUNTIME_ENVIRONMENT_MODEL__HARDWARE_SPECIFICATIONS:
				getHardwareSpecifications().clear();
				getHardwareSpecifications().addAll((Collection<? extends HardwareInformation>)newValue);
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
		}
		return super.eIsSet(featureID);
	}

} //RuntimeEnvironmentModelImpl
