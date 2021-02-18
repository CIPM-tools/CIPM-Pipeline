/**
 */
package cipm.consistency.base.models.scg.ServiceCallGraph;

import org.eclipse.emf.ecore.EObject;

import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode#getSeff <em>Seff</em>}</li>
 *   <li>{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode#getHost <em>Host</em>}</li>
 * </ul>
 *
 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraphNode()
 * @model
 * @generated
 */
public interface ServiceCallGraphNode extends EObject {
	/**
	 * Returns the value of the '<em><b>Seff</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Seff</em>' reference.
	 * @see #setSeff(ResourceDemandingSEFF)
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraphNode_Seff()
	 * @model
	 * @generated
	 */
	ResourceDemandingSEFF getSeff();

	/**
	 * Sets the value of the '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode#getSeff <em>Seff</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Seff</em>' reference.
	 * @see #getSeff()
	 * @generated
	 */
	void setSeff(ResourceDemandingSEFF value);

	/**
	 * Returns the value of the '<em><b>Host</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Host</em>' reference.
	 * @see #setHost(ResourceContainer)
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraphNode_Host()
	 * @model
	 * @generated
	 */
	ResourceContainer getHost();

	/**
	 * Sets the value of the '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode#getHost <em>Host</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Host</em>' reference.
	 * @see #getHost()
	 * @generated
	 */
	void setHost(ResourceContainer value);

} // ServiceCallGraphNode
