/**
 */
package dmodel.base.models.callgraph.ServiceCallGraph.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.palladiosimulator.pcm.seff.ExternalCallAction;

import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphEdge;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphNode;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link dmodel.base.models.callgraph.ServiceCallGraph.impl.ServiceCallGraphEdgeImpl#getFrom <em>From</em>}</li>
 *   <li>{@link dmodel.base.models.callgraph.ServiceCallGraph.impl.ServiceCallGraphEdgeImpl#getTo <em>To</em>}</li>
 *   <li>{@link dmodel.base.models.callgraph.ServiceCallGraph.impl.ServiceCallGraphEdgeImpl#getValue <em>Value</em>}</li>
 *   <li>{@link dmodel.base.models.callgraph.ServiceCallGraph.impl.ServiceCallGraphEdgeImpl#getExternalCall <em>External Call</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ServiceCallGraphEdgeImpl extends MinimalEObjectImpl.Container implements ServiceCallGraphEdge {
	/**
	 * The cached value of the '{@link #getFrom() <em>From</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFrom()
	 * @generated
	 * @ordered
	 */
	protected ServiceCallGraphNode from;

	/**
	 * The cached value of the '{@link #getTo() <em>To</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTo()
	 * @generated
	 * @ordered
	 */
	protected ServiceCallGraphNode to;

	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final int VALUE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected int value = VALUE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getExternalCall() <em>External Call</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExternalCall()
	 * @generated
	 * @ordered
	 */
	protected ExternalCallAction externalCall;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ServiceCallGraphEdgeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ServiceCallGraphPackage.Literals.SERVICE_CALL_GRAPH_EDGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ServiceCallGraphNode getFrom() {
		if (from != null && from.eIsProxy()) {
			InternalEObject oldFrom = (InternalEObject)from;
			from = (ServiceCallGraphNode)eResolveProxy(oldFrom);
			if (from != oldFrom) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__FROM, oldFrom, from));
			}
		}
		return from;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ServiceCallGraphNode basicGetFrom() {
		return from;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFrom(ServiceCallGraphNode newFrom) {
		ServiceCallGraphNode oldFrom = from;
		from = newFrom;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__FROM, oldFrom, from));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ServiceCallGraphNode getTo() {
		if (to != null && to.eIsProxy()) {
			InternalEObject oldTo = (InternalEObject)to;
			to = (ServiceCallGraphNode)eResolveProxy(oldTo);
			if (to != oldTo) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__TO, oldTo, to));
			}
		}
		return to;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ServiceCallGraphNode basicGetTo() {
		return to;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTo(ServiceCallGraphNode newTo) {
		ServiceCallGraphNode oldTo = to;
		to = newTo;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__TO, oldTo, to));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValue(int newValue) {
		int oldValue = value;
		value = newValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__VALUE, oldValue, value));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ExternalCallAction getExternalCall() {
		if (externalCall != null && ((EObject)externalCall).eIsProxy()) {
			InternalEObject oldExternalCall = (InternalEObject)externalCall;
			externalCall = (ExternalCallAction)eResolveProxy(oldExternalCall);
			if (externalCall != oldExternalCall) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__EXTERNAL_CALL, oldExternalCall, externalCall));
			}
		}
		return externalCall;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExternalCallAction basicGetExternalCall() {
		return externalCall;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExternalCall(ExternalCallAction newExternalCall) {
		ExternalCallAction oldExternalCall = externalCall;
		externalCall = newExternalCall;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__EXTERNAL_CALL, oldExternalCall, externalCall));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__FROM:
				if (resolve) return getFrom();
				return basicGetFrom();
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__TO:
				if (resolve) return getTo();
				return basicGetTo();
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__VALUE:
				return getValue();
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__EXTERNAL_CALL:
				if (resolve) return getExternalCall();
				return basicGetExternalCall();
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
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__FROM:
				setFrom((ServiceCallGraphNode)newValue);
				return;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__TO:
				setTo((ServiceCallGraphNode)newValue);
				return;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__VALUE:
				setValue((Integer)newValue);
				return;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__EXTERNAL_CALL:
				setExternalCall((ExternalCallAction)newValue);
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
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__FROM:
				setFrom((ServiceCallGraphNode)null);
				return;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__TO:
				setTo((ServiceCallGraphNode)null);
				return;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__VALUE:
				setValue(VALUE_EDEFAULT);
				return;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__EXTERNAL_CALL:
				setExternalCall((ExternalCallAction)null);
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
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__FROM:
				return from != null;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__TO:
				return to != null;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__VALUE:
				return value != VALUE_EDEFAULT;
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE__EXTERNAL_CALL:
				return externalCall != null;
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
		result.append(" (value: ");
		result.append(value);
		result.append(')');
		return result.toString();
	}

} //ServiceCallGraphEdgeImpl
