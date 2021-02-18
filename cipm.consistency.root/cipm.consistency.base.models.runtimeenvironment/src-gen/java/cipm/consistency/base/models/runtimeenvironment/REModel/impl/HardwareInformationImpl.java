/**
 */
package cipm.consistency.base.models.runtimeenvironment.REModel.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.palladiosimulator.pcm.core.entity.impl.EntityImpl;

import cipm.consistency.base.models.runtimeenvironment.REModel.HardwareInformation;
import cipm.consistency.base.models.runtimeenvironment.REModel.REModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Hardware Information</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link cipm.consistency.base.models.runtimeenvironment.REModel.impl.HardwareInformationImpl#getCores <em>Cores</em>}</li>
 *   <li>{@link cipm.consistency.base.models.runtimeenvironment.REModel.impl.HardwareInformationImpl#getMainMemorySize <em>Main Memory Size</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HardwareInformationImpl extends EntityImpl implements HardwareInformation {
	/**
	 * The default value of the '{@link #getCores() <em>Cores</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCores()
	 * @generated
	 * @ordered
	 */
	protected static final int CORES_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getCores() <em>Cores</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCores()
	 * @generated
	 * @ordered
	 */
	protected int cores = CORES_EDEFAULT;

	/**
	 * The default value of the '{@link #getMainMemorySize() <em>Main Memory Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMainMemorySize()
	 * @generated
	 * @ordered
	 */
	protected static final long MAIN_MEMORY_SIZE_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getMainMemorySize() <em>Main Memory Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMainMemorySize()
	 * @generated
	 * @ordered
	 */
	protected long mainMemorySize = MAIN_MEMORY_SIZE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HardwareInformationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return REModelPackage.Literals.HARDWARE_INFORMATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getCores() {
		return cores;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCores(int newCores) {
		int oldCores = cores;
		cores = newCores;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, REModelPackage.HARDWARE_INFORMATION__CORES, oldCores, cores));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getMainMemorySize() {
		return mainMemorySize;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMainMemorySize(long newMainMemorySize) {
		long oldMainMemorySize = mainMemorySize;
		mainMemorySize = newMainMemorySize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, REModelPackage.HARDWARE_INFORMATION__MAIN_MEMORY_SIZE, oldMainMemorySize, mainMemorySize));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case REModelPackage.HARDWARE_INFORMATION__CORES:
				return getCores();
			case REModelPackage.HARDWARE_INFORMATION__MAIN_MEMORY_SIZE:
				return getMainMemorySize();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case REModelPackage.HARDWARE_INFORMATION__CORES:
				setCores((Integer)newValue);
				return;
			case REModelPackage.HARDWARE_INFORMATION__MAIN_MEMORY_SIZE:
				setMainMemorySize((Long)newValue);
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
			case REModelPackage.HARDWARE_INFORMATION__CORES:
				setCores(CORES_EDEFAULT);
				return;
			case REModelPackage.HARDWARE_INFORMATION__MAIN_MEMORY_SIZE:
				setMainMemorySize(MAIN_MEMORY_SIZE_EDEFAULT);
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
			case REModelPackage.HARDWARE_INFORMATION__CORES:
				return cores != CORES_EDEFAULT;
			case REModelPackage.HARDWARE_INFORMATION__MAIN_MEMORY_SIZE:
				return mainMemorySize != MAIN_MEMORY_SIZE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (cores: ");
		result.append(cores);
		result.append(", mainMemorySize: ");
		result.append(mainMemorySize);
		result.append(')');
		return result.toString();
	}

} //HardwareInformationImpl
