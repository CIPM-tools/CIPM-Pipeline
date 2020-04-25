/**
 */
package dmodel.base.models.callgraph.ServiceCallGraph;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphPackage
 * @generated
 */
public interface ServiceCallGraphFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ServiceCallGraphFactory eINSTANCE = dmodel.base.models.callgraph.ServiceCallGraph.impl.ServiceCallGraphFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Service Call Graph</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Service Call Graph</em>'.
	 * @generated
	 */
	ServiceCallGraph createServiceCallGraph();

	/**
	 * Returns a new object of class '<em>Edge</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Edge</em>'.
	 * @generated
	 */
	ServiceCallGraphEdge createServiceCallGraphEdge();

	/**
	 * Returns a new object of class '<em>Node</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Node</em>'.
	 * @generated
	 */
	ServiceCallGraphNode createServiceCallGraphNode();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ServiceCallGraphPackage getServiceCallGraphPackage();

} //ServiceCallGraphFactory
