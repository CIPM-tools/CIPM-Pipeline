/**
 */
package cipm.consistency.base.models.scg.ServiceCallGraph;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphFactory
 * @model kind="package"
 * @generated
 */
public interface ServiceCallGraphPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "ServiceCallGraph";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.dmodel.com/CallGraph";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "scg";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ServiceCallGraphPackage eINSTANCE = cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphPackageImpl.init();

	/**
	 * The meta object id for the '{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphImpl <em>Service Call Graph</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphImpl
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphPackageImpl#getServiceCallGraph()
	 * @generated
	 */
	int SERVICE_CALL_GRAPH = 0;

	/**
	 * The feature id for the '<em><b>Nodes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH__NODES = 0;

	/**
	 * The feature id for the '<em><b>Edges</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH__EDGES = 1;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH__OUTGOING_EDGES = 2;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH__INCOMING_EDGES = 3;

	/**
	 * The number of structural features of the '<em>Service Call Graph</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphEdgeImpl <em>Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphEdgeImpl
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphPackageImpl#getServiceCallGraphEdge()
	 * @generated
	 */
	int SERVICE_CALL_GRAPH_EDGE = 1;

	/**
	 * The feature id for the '<em><b>From</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH_EDGE__FROM = 0;

	/**
	 * The feature id for the '<em><b>To</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH_EDGE__TO = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH_EDGE__VALUE = 2;

	/**
	 * The feature id for the '<em><b>External Call</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH_EDGE__EXTERNAL_CALL = 3;

	/**
	 * The number of structural features of the '<em>Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH_EDGE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.EdgeListImpl <em>Edge List</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.EdgeListImpl
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphPackageImpl#getEdgeList()
	 * @generated
	 */
	int EDGE_LIST = 2;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE_LIST__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE_LIST__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Edge List</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE_LIST_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphNodeImpl <em>Node</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphNodeImpl
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphPackageImpl#getServiceCallGraphNode()
	 * @generated
	 */
	int SERVICE_CALL_GRAPH_NODE = 3;

	/**
	 * The feature id for the '<em><b>Seff</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH_NODE__SEFF = 0;

	/**
	 * The feature id for the '<em><b>Host</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH_NODE__HOST = 1;

	/**
	 * The number of structural features of the '<em>Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_CALL_GRAPH_NODE_FEATURE_COUNT = 2;


	/**
	 * Returns the meta object for class '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph <em>Service Call Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Service Call Graph</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph
	 * @generated
	 */
	EClass getServiceCallGraph();

	/**
	 * Returns the meta object for the containment reference list '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph#getNodes <em>Nodes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nodes</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph#getNodes()
	 * @see #getServiceCallGraph()
	 * @generated
	 */
	EReference getServiceCallGraph_Nodes();

	/**
	 * Returns the meta object for the containment reference list '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph#getEdges <em>Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Edges</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph#getEdges()
	 * @see #getServiceCallGraph()
	 * @generated
	 */
	EReference getServiceCallGraph_Edges();

	/**
	 * Returns the meta object for the map '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph#getOutgoingEdges <em>Outgoing Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Outgoing Edges</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph#getOutgoingEdges()
	 * @see #getServiceCallGraph()
	 * @generated
	 */
	EReference getServiceCallGraph_OutgoingEdges();

	/**
	 * Returns the meta object for the map '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph#getIncomingEdges <em>Incoming Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Incoming Edges</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraph#getIncomingEdges()
	 * @see #getServiceCallGraph()
	 * @generated
	 */
	EReference getServiceCallGraph_IncomingEdges();

	/**
	 * Returns the meta object for class '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge <em>Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Edge</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge
	 * @generated
	 */
	EClass getServiceCallGraphEdge();

	/**
	 * Returns the meta object for the reference '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge#getFrom <em>From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>From</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge#getFrom()
	 * @see #getServiceCallGraphEdge()
	 * @generated
	 */
	EReference getServiceCallGraphEdge_From();

	/**
	 * Returns the meta object for the reference '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge#getTo <em>To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>To</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge#getTo()
	 * @see #getServiceCallGraphEdge()
	 * @generated
	 */
	EReference getServiceCallGraphEdge_To();

	/**
	 * Returns the meta object for the attribute '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge#getValue()
	 * @see #getServiceCallGraphEdge()
	 * @generated
	 */
	EAttribute getServiceCallGraphEdge_Value();

	/**
	 * Returns the meta object for the reference '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge#getExternalCall <em>External Call</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>External Call</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphEdge#getExternalCall()
	 * @see #getServiceCallGraphEdge()
	 * @generated
	 */
	EReference getServiceCallGraphEdge_ExternalCall();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Edge List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Edge List</em>'.
	 * @see java.util.Map.Entry
	 * @model keyUnique="false" keyDataType="org.eclipse.emf.ecore.EJavaObject"
	 *        valueType="dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge" valueMany="true"
	 * @generated
	 */
	EClass getEdgeList();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getEdgeList()
	 * @generated
	 */
	EAttribute getEdgeList_Key();

	/**
	 * Returns the meta object for the reference list '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getEdgeList()
	 * @generated
	 */
	EReference getEdgeList_Value();

	/**
	 * Returns the meta object for class '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Node</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode
	 * @generated
	 */
	EClass getServiceCallGraphNode();

	/**
	 * Returns the meta object for the reference '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode#getSeff <em>Seff</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Seff</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode#getSeff()
	 * @see #getServiceCallGraphNode()
	 * @generated
	 */
	EReference getServiceCallGraphNode_Seff();

	/**
	 * Returns the meta object for the reference '{@link cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode#getHost <em>Host</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Host</em>'.
	 * @see cipm.consistency.base.models.scg.ServiceCallGraph.ServiceCallGraphNode#getHost()
	 * @see #getServiceCallGraphNode()
	 * @generated
	 */
	EReference getServiceCallGraphNode_Host();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ServiceCallGraphFactory getServiceCallGraphFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphImpl <em>Service Call Graph</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphImpl
		 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphPackageImpl#getServiceCallGraph()
		 * @generated
		 */
		EClass SERVICE_CALL_GRAPH = eINSTANCE.getServiceCallGraph();

		/**
		 * The meta object literal for the '<em><b>Nodes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_CALL_GRAPH__NODES = eINSTANCE.getServiceCallGraph_Nodes();

		/**
		 * The meta object literal for the '<em><b>Edges</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_CALL_GRAPH__EDGES = eINSTANCE.getServiceCallGraph_Edges();

		/**
		 * The meta object literal for the '<em><b>Outgoing Edges</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_CALL_GRAPH__OUTGOING_EDGES = eINSTANCE.getServiceCallGraph_OutgoingEdges();

		/**
		 * The meta object literal for the '<em><b>Incoming Edges</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_CALL_GRAPH__INCOMING_EDGES = eINSTANCE.getServiceCallGraph_IncomingEdges();

		/**
		 * The meta object literal for the '{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphEdgeImpl <em>Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphEdgeImpl
		 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphPackageImpl#getServiceCallGraphEdge()
		 * @generated
		 */
		EClass SERVICE_CALL_GRAPH_EDGE = eINSTANCE.getServiceCallGraphEdge();

		/**
		 * The meta object literal for the '<em><b>From</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_CALL_GRAPH_EDGE__FROM = eINSTANCE.getServiceCallGraphEdge_From();

		/**
		 * The meta object literal for the '<em><b>To</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_CALL_GRAPH_EDGE__TO = eINSTANCE.getServiceCallGraphEdge_To();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVICE_CALL_GRAPH_EDGE__VALUE = eINSTANCE.getServiceCallGraphEdge_Value();

		/**
		 * The meta object literal for the '<em><b>External Call</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_CALL_GRAPH_EDGE__EXTERNAL_CALL = eINSTANCE.getServiceCallGraphEdge_ExternalCall();

		/**
		 * The meta object literal for the '{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.EdgeListImpl <em>Edge List</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.EdgeListImpl
		 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphPackageImpl#getEdgeList()
		 * @generated
		 */
		EClass EDGE_LIST = eINSTANCE.getEdgeList();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDGE_LIST__KEY = eINSTANCE.getEdgeList_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EDGE_LIST__VALUE = eINSTANCE.getEdgeList_Value();

		/**
		 * The meta object literal for the '{@link cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphNodeImpl <em>Node</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphNodeImpl
		 * @see cipm.consistency.base.models.scg.ServiceCallGraph.impl.ServiceCallGraphPackageImpl#getServiceCallGraphNode()
		 * @generated
		 */
		EClass SERVICE_CALL_GRAPH_NODE = eINSTANCE.getServiceCallGraphNode();

		/**
		 * The meta object literal for the '<em><b>Seff</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_CALL_GRAPH_NODE__SEFF = eINSTANCE.getServiceCallGraphNode_Seff();

		/**
		 * The meta object literal for the '<em><b>Host</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_CALL_GRAPH_NODE__HOST = eINSTANCE.getServiceCallGraphNode_Host();

	}

} //ServiceCallGraphPackage
