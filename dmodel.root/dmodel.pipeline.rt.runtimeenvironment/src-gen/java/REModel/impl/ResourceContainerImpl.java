/**
 */
package REModel.impl;

import REModel.HardwareInformation;
import REModel.REModelPackage;
import REModel.ResourceContainer;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Resource Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link REModel.impl.ResourceContainerImpl#getHostname <em>Hostname</em>}</li>
 *   <li>{@link REModel.impl.ResourceContainerImpl#getHostID <em>Host ID</em>}</li>
 *   <li>{@link REModel.impl.ResourceContainerImpl#getHardware <em>Hardware</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ResourceContainerImpl extends MinimalEObjectImpl.Container implements ResourceContainer {
	/**
	 * The default value of the '{@link #getHostname() <em>Hostname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHostname()
	 * @generated
	 * @ordered
	 */
	protected static final String HOSTNAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getHostname() <em>Hostname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHostname()
	 * @generated
	 * @ordered
	 */
	protected String hostname = HOSTNAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getHostID() <em>Host ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHostID()
	 * @generated
	 * @ordered
	 */
	protected static final String HOST_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getHostID() <em>Host ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHostID()
	 * @generated
	 * @ordered
	 */
	protected String hostID = HOST_ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getHardware() <em>Hardware</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHardware()
	 * @generated
	 * @ordered
	 */
	protected HardwareInformation hardware;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ResourceContainerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return REModelPackage.Literals.RESOURCE_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHostname(String newHostname) {
		String oldHostname = hostname;
		hostname = newHostname;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, REModelPackage.RESOURCE_CONTAINER__HOSTNAME, oldHostname, hostname));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getHostID() {
		return hostID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHostID(String newHostID) {
		String oldHostID = hostID;
		hostID = newHostID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, REModelPackage.RESOURCE_CONTAINER__HOST_ID, oldHostID, hostID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HardwareInformation getHardware() {
		if (hardware != null && hardware.eIsProxy()) {
			InternalEObject oldHardware = (InternalEObject)hardware;
			hardware = (HardwareInformation)eResolveProxy(oldHardware);
			if (hardware != oldHardware) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, REModelPackage.RESOURCE_CONTAINER__HARDWARE, oldHardware, hardware));
			}
		}
		return hardware;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HardwareInformation basicGetHardware() {
		return hardware;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHardware(HardwareInformation newHardware) {
		HardwareInformation oldHardware = hardware;
		hardware = newHardware;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, REModelPackage.RESOURCE_CONTAINER__HARDWARE, oldHardware, hardware));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case REModelPackage.RESOURCE_CONTAINER__HOSTNAME:
				return getHostname();
			case REModelPackage.RESOURCE_CONTAINER__HOST_ID:
				return getHostID();
			case REModelPackage.RESOURCE_CONTAINER__HARDWARE:
				if (resolve) return getHardware();
				return basicGetHardware();
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
			case REModelPackage.RESOURCE_CONTAINER__HOSTNAME:
				setHostname((String)newValue);
				return;
			case REModelPackage.RESOURCE_CONTAINER__HOST_ID:
				setHostID((String)newValue);
				return;
			case REModelPackage.RESOURCE_CONTAINER__HARDWARE:
				setHardware((HardwareInformation)newValue);
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
			case REModelPackage.RESOURCE_CONTAINER__HOSTNAME:
				setHostname(HOSTNAME_EDEFAULT);
				return;
			case REModelPackage.RESOURCE_CONTAINER__HOST_ID:
				setHostID(HOST_ID_EDEFAULT);
				return;
			case REModelPackage.RESOURCE_CONTAINER__HARDWARE:
				setHardware((HardwareInformation)null);
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
			case REModelPackage.RESOURCE_CONTAINER__HOSTNAME:
				return HOSTNAME_EDEFAULT == null ? hostname != null : !HOSTNAME_EDEFAULT.equals(hostname);
			case REModelPackage.RESOURCE_CONTAINER__HOST_ID:
				return HOST_ID_EDEFAULT == null ? hostID != null : !HOST_ID_EDEFAULT.equals(hostID);
			case REModelPackage.RESOURCE_CONTAINER__HARDWARE:
				return hardware != null;
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
		result.append(" (hostname: ");
		result.append(hostname);
		result.append(", hostID: ");
		result.append(hostID);
		result.append(')');
		return result.toString();
	}

} //ResourceContainerImpl
