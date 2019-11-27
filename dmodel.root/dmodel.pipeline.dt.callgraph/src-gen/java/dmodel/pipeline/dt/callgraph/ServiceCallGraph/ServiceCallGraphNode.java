/**
 */
package dmodel.pipeline.dt.callgraph.ServiceCallGraph;

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
 *   <li>{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode#getSeff <em>Seff</em>}</li>
 *   <li>{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode#getHost <em>Host</em>}</li>
 * </ul>
 *
 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraphNode()
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
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraphNode_Seff()
	 * @model
	 * @generated
	 */
	ResourceDemandingSEFF getSeff();

	/**
	 * Sets the value of the '{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode#getSeff <em>Seff</em>}' reference.
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
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraphNode_Host()
	 * @model
	 * @generated
	 */
	ResourceContainer getHost();

	/**
	 * Sets the value of the '{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode#getHost <em>Host</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Host</em>' reference.
	 * @see #getHost()
	 * @generated
	 */
	void setHost(ResourceContainer value);

} // ServiceCallGraphNode
