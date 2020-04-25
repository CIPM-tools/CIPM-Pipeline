/**
 */
package dmodel.base.models.callgraph.ServiceCallGraph.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphNode;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link dmodel.base.models.callgraph.ServiceCallGraph.impl.ServiceCallGraphNodeImpl#getSeff <em>Seff</em>}</li>
 *   <li>{@link dmodel.base.models.callgraph.ServiceCallGraph.impl.ServiceCallGraphNodeImpl#getHost <em>Host</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ServiceCallGraphNodeImpl extends MinimalEObjectImpl.Container implements ServiceCallGraphNode {
	/**
	 * The cached value of the '{@link #getSeff() <em>Seff</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSeff()
	 * @generated
	 * @ordered
	 */
	protected ResourceDemandingSEFF seff;

	/**
	 * The cached value of the '{@link #getHost() <em>Host</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHost()
	 * @generated
	 * @ordered
	 */
	protected ResourceContainer host;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ServiceCallGraphNodeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ServiceCallGraphPackage.Literals.SERVICE_CALL_GRAPH_NODE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceDemandingSEFF getSeff() {
		if (seff != null && ((EObject)seff).eIsProxy()) {
			InternalEObject oldSeff = (InternalEObject)seff;
			seff = (ResourceDemandingSEFF)eResolveProxy(oldSeff);
			if (seff != oldSeff) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__SEFF, oldSeff, seff));
			}
		}
		return seff;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResourceDemandingSEFF basicGetSeff() {
		return seff;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSeff(ResourceDemandingSEFF newSeff) {
		ResourceDemandingSEFF oldSeff = seff;
		seff = newSeff;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__SEFF, oldSeff, seff));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceContainer getHost() {
		if (host != null && ((EObject)host).eIsProxy()) {
			InternalEObject oldHost = (InternalEObject)host;
			host = (ResourceContainer)eResolveProxy(oldHost);
			if (host != oldHost) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__HOST, oldHost, host));
			}
		}
		return host;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResourceContainer basicGetHost() {
		return host;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setHost(ResourceContainer newHost) {
		ResourceContainer oldHost = host;
		host = newHost;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__HOST, oldHost, host));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__SEFF:
				if (resolve) return getSeff();
				return basicGetSeff();
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__HOST:
				if (resolve) return getHost();
				return basicGetHost();
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
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__SEFF:
				setSeff((ResourceDemandingSEFF)newValue);
				return;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__HOST:
				setHost((ResourceContainer)newValue);
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
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__SEFF:
				setSeff((ResourceDemandingSEFF)null);
				return;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__HOST:
				setHost((ResourceContainer)null);
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
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__SEFF:
				return seff != null;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE__HOST:
				return host != null;
		}
		return super.eIsSet(featureID);
	}

} //ServiceCallGraphNodeImpl
