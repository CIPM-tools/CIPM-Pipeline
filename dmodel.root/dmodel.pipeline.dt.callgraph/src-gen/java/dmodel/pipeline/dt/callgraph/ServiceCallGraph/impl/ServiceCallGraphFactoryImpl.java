/**
 */
package dmodel.pipeline.dt.callgraph.ServiceCallGraph.impl;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.*;

import java.util.Map;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ServiceCallGraphFactoryImpl extends EFactoryImpl implements ServiceCallGraphFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ServiceCallGraphFactory init() {
		try {
			ServiceCallGraphFactory theServiceCallGraphFactory = (ServiceCallGraphFactory)EPackage.Registry.INSTANCE.getEFactory(ServiceCallGraphPackage.eNS_URI);
			if (theServiceCallGraphFactory != null) {
				return theServiceCallGraphFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ServiceCallGraphFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ServiceCallGraphFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH: return createServiceCallGraph();
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_EDGE: return createServiceCallGraphEdge();
			case ServiceCallGraphPackage.EDGE_LIST: return (EObject)createEdgeList();
			case ServiceCallGraphPackage.SERVICE_CALL_GRAPH_NODE: return createServiceCallGraphNode();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ServiceCallGraph createServiceCallGraph() {
		ServiceCallGraphImpl serviceCallGraph = new ServiceCallGraphImpl();
		return serviceCallGraph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ServiceCallGraphEdge createServiceCallGraphEdge() {
		ServiceCallGraphEdgeImpl serviceCallGraphEdge = new ServiceCallGraphEdgeImpl();
		return serviceCallGraphEdge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<Object, EList<ServiceCallGraphEdge>> createEdgeList() {
		EdgeListImpl edgeList = new EdgeListImpl();
		return edgeList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ServiceCallGraphNode createServiceCallGraphNode() {
		ServiceCallGraphNodeImpl serviceCallGraphNode = new ServiceCallGraphNodeImpl();
		return serviceCallGraphNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ServiceCallGraphPackage getServiceCallGraphPackage() {
		return (ServiceCallGraphPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ServiceCallGraphPackage getPackage() {
		return ServiceCallGraphPackage.eINSTANCE;
	}

} //ServiceCallGraphFactoryImpl
