/**
 */
package dmodel.base.models.runtimeenvironment.REModel.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.palladiosimulator.pcm.core.entity.impl.EntityImpl;

import dmodel.base.models.runtimeenvironment.REModel.ConnectionSpecification;
import dmodel.base.models.runtimeenvironment.REModel.REModelPackage;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Runtime Resource Container Connection</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeResourceContainerConnectionImpl#getContainerFrom <em>Container From</em>}</li>
 *   <li>{@link dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeResourceContainerConnectionImpl#getContainerTo <em>Container To</em>}</li>
 *   <li>{@link dmodel.base.models.runtimeenvironment.REModel.impl.RuntimeResourceContainerConnectionImpl#getConnectionSpecification <em>Connection Specification</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RuntimeResourceContainerConnectionImpl extends EntityImpl implements RuntimeResourceContainerConnection {
	/**
	 * The cached value of the '{@link #getContainerFrom() <em>Container From</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContainerFrom()
	 * @generated
	 * @ordered
	 */
	protected RuntimeResourceContainer containerFrom;

	/**
	 * The cached value of the '{@link #getContainerTo() <em>Container To</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContainerTo()
	 * @generated
	 * @ordered
	 */
	protected RuntimeResourceContainer containerTo;

	/**
	 * The cached value of the '{@link #getConnectionSpecification() <em>Connection Specification</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConnectionSpecification()
	 * @generated
	 * @ordered
	 */
	protected ConnectionSpecification connectionSpecification;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RuntimeResourceContainerConnectionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return REModelPackage.Literals.RUNTIME_RESOURCE_CONTAINER_CONNECTION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeResourceContainer getContainerFrom() {
		if (containerFrom != null && containerFrom.eIsProxy()) {
			InternalEObject oldContainerFrom = (InternalEObject)containerFrom;
			containerFrom = (RuntimeResourceContainer)eResolveProxy(oldContainerFrom);
			if (containerFrom != oldContainerFrom) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_FROM, oldContainerFrom, containerFrom));
			}
		}
		return containerFrom;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeResourceContainer basicGetContainerFrom() {
		return containerFrom;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContainerFrom(RuntimeResourceContainer newContainerFrom) {
		RuntimeResourceContainer oldContainerFrom = containerFrom;
		containerFrom = newContainerFrom;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_FROM, oldContainerFrom, containerFrom));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeResourceContainer getContainerTo() {
		if (containerTo != null && containerTo.eIsProxy()) {
			InternalEObject oldContainerTo = (InternalEObject)containerTo;
			containerTo = (RuntimeResourceContainer)eResolveProxy(oldContainerTo);
			if (containerTo != oldContainerTo) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_TO, oldContainerTo, containerTo));
			}
		}
		return containerTo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RuntimeResourceContainer basicGetContainerTo() {
		return containerTo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContainerTo(RuntimeResourceContainer newContainerTo) {
		RuntimeResourceContainer oldContainerTo = containerTo;
		containerTo = newContainerTo;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_TO, oldContainerTo, containerTo));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConnectionSpecification getConnectionSpecification() {
		if (connectionSpecification != null && connectionSpecification.eIsProxy()) {
			InternalEObject oldConnectionSpecification = (InternalEObject)connectionSpecification;
			connectionSpecification = (ConnectionSpecification)eResolveProxy(oldConnectionSpecification);
			if (connectionSpecification != oldConnectionSpecification) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONNECTION_SPECIFICATION, oldConnectionSpecification, connectionSpecification));
			}
		}
		return connectionSpecification;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConnectionSpecification basicGetConnectionSpecification() {
		return connectionSpecification;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConnectionSpecification(ConnectionSpecification newConnectionSpecification) {
		ConnectionSpecification oldConnectionSpecification = connectionSpecification;
		connectionSpecification = newConnectionSpecification;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONNECTION_SPECIFICATION, oldConnectionSpecification, connectionSpecification));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_FROM:
				if (resolve) return getContainerFrom();
				return basicGetContainerFrom();
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_TO:
				if (resolve) return getContainerTo();
				return basicGetContainerTo();
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONNECTION_SPECIFICATION:
				if (resolve) return getConnectionSpecification();
				return basicGetConnectionSpecification();
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
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_FROM:
				setContainerFrom((RuntimeResourceContainer)newValue);
				return;
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_TO:
				setContainerTo((RuntimeResourceContainer)newValue);
				return;
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONNECTION_SPECIFICATION:
				setConnectionSpecification((ConnectionSpecification)newValue);
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
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_FROM:
				setContainerFrom((RuntimeResourceContainer)null);
				return;
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_TO:
				setContainerTo((RuntimeResourceContainer)null);
				return;
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONNECTION_SPECIFICATION:
				setConnectionSpecification((ConnectionSpecification)null);
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
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_FROM:
				return containerFrom != null;
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONTAINER_TO:
				return containerTo != null;
			case REModelPackage.RUNTIME_RESOURCE_CONTAINER_CONNECTION__CONNECTION_SPECIFICATION:
				return connectionSpecification != null;
		}
		return super.eIsSet(featureID);
	}

} //RuntimeResourceContainerConnectionImpl
