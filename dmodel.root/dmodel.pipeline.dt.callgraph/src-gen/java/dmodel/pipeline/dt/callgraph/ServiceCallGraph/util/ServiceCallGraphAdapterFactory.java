/**
 */
package dmodel.pipeline.dt.callgraph.ServiceCallGraph.util;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.*;

import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphPackage
 * @generated
 */
public class ServiceCallGraphAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ServiceCallGraphPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ServiceCallGraphAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = ServiceCallGraphPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ServiceCallGraphSwitch<Adapter> modelSwitch =
		new ServiceCallGraphSwitch<Adapter>() {
			@Override
			public Adapter caseServiceCallGraph(ServiceCallGraph object) {
				return createServiceCallGraphAdapter();
			}
			@Override
			public Adapter caseServiceCallGraphEdge(ServiceCallGraphEdge object) {
				return createServiceCallGraphEdgeAdapter();
			}
			@Override
			public Adapter caseEdgeList(Map.Entry<Object, EList<ServiceCallGraphEdge>> object) {
				return createEdgeListAdapter();
			}
			@Override
			public Adapter caseServiceCallGraphNode(ServiceCallGraphNode object) {
				return createServiceCallGraphNodeAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph <em>Service Call Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph
	 * @generated
	 */
	public Adapter createServiceCallGraphAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge <em>Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphEdge
	 * @generated
	 */
	public Adapter createServiceCallGraphEdgeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Edge List</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createEdgeListAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraphNode
	 * @generated
	 */
	public Adapter createServiceCallGraphNodeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //ServiceCallGraphAdapterFactory
