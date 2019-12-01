/**
 */
package dmodel.pipeline.dt.callgraph.ServiceCallGraph;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Service Call Graph</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph#getNodes <em>Nodes</em>}</li>
 *   <li>{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph#getEdges <em>Edges</em>}</li>
 *   <li>{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph#getOutgoingEdges <em>Outgoing Edges</em>}</li>
 *   <li>{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph#getIncomingEdges <em>Incoming Edges</em>}</li>
 * </ul>
 *
 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraph()
 * @model
 * @generated
 */
public interface ServiceCallGraph extends EObject {
	/**
	 * Returns the value of the '<em><b>Nodes</b></em>' reference list.
	 * The list contents are of type {@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nodes</em>' reference list.
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraph_Nodes()
	 * @model
	 * @generated
	 */
	EList<ServiceCallGraphNode> getNodes();

	/**
	 * Returns the value of the '<em><b>Edges</b></em>' containment reference list.
	 * The list contents are of type {@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Edges</em>' containment reference list.
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraph_Edges()
	 * @model containment="true"
	 * @generated
	 */
	EList<ServiceCallGraphEdge> getEdges();

	/**
	 * Returns the value of the '<em><b>Outgoing Edges</b></em>' map.
	 * The key is of type {@link java.lang.Object},
	 * and the value is of type list of {@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Outgoing Edges</em>' map.
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraph_OutgoingEdges()
	 * @model mapType="dmodel.pipeline.dt.callgraph.ServiceCallGraph.EdgeList&lt;org.eclipse.emf.ecore.EJavaObject, dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge&gt;"
	 * @generated
	 */
	EMap<Object, EList<ServiceCallGraphEdge>> getOutgoingEdges();

	/**
	 * Returns the value of the '<em><b>Incoming Edges</b></em>' map.
	 * The key is of type {@link java.lang.Object},
	 * and the value is of type list of {@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Incoming Edges</em>' map.
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage#getServiceCallGraph_IncomingEdges()
	 * @model mapType="dmodel.pipeline.dt.callgraph.ServiceCallGraph.EdgeList&lt;org.eclipse.emf.ecore.EJavaObject, dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge&gt;"
	 * @generated
	 */
	EMap<Object, EList<ServiceCallGraphEdge>> getIncomingEdges();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	ServiceCallGraphNode addNode(ResourceDemandingSEFF seff, ResourceContainer host);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void addEdge(ResourceDemandingSEFF from, ResourceDemandingSEFF to, ResourceContainer fromContainer, ResourceContainer toContainer, int value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void incrementEdge(ResourceDemandingSEFF from, ResourceDemandingSEFF to, ResourceContainer fromContainer, ResourceContainer toContainer);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	ServiceCallGraphEdge hasEdge(ResourceDemandingSEFF from, ResourceDemandingSEFF to, ResourceContainer fromContainer, ResourceContainer toContainer);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	ServiceCallGraphNode hasNode(ResourceDemandingSEFF node, ResourceContainer host);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	boolean nodeEqual(ResourceDemandingSEFF node, ResourceContainer host, ResourceDemandingSEFF node2, ResourceContainer host2);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void removeEdge(ServiceCallGraphEdge edge);

} // ServiceCallGraph