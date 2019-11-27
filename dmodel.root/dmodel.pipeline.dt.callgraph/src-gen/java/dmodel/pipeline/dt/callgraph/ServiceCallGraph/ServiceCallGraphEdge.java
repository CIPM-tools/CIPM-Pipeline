/**
 */
package dmodel.pipeline.dt.callgraph.ServiceCallGraph;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge#getFrom <em>From</em>}</li>
 *   <li>{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge#getTo <em>To</em>}</li>
 *   <li>{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraphEdge()
 * @model
 * @generated
 */
public interface ServiceCallGraphEdge extends EObject {
	/**
	 * Returns the value of the '<em><b>From</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>From</em>' reference.
	 * @see #setFrom(ServiceCallGraphNode)
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraphEdge_From()
	 * @model
	 * @generated
	 */
	ServiceCallGraphNode getFrom();

	/**
	 * Sets the value of the '{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge#getFrom <em>From</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>From</em>' reference.
	 * @see #getFrom()
	 * @generated
	 */
	void setFrom(ServiceCallGraphNode value);

	/**
	 * Returns the value of the '<em><b>To</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>To</em>' reference.
	 * @see #setTo(ServiceCallGraphNode)
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraphEdge_To()
	 * @model
	 * @generated
	 */
	ServiceCallGraphNode getTo();

	/**
	 * Sets the value of the '{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge#getTo <em>To</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>To</em>' reference.
	 * @see #getTo()
	 * @generated
	 */
	void setTo(ServiceCallGraphNode value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(int)
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraphEdge_Value()
	 * @model unique="false"
	 * @generated
	 */
	int getValue();

	/**
	 * Sets the value of the '{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(int value);

} // ServiceCallGraphEdge
