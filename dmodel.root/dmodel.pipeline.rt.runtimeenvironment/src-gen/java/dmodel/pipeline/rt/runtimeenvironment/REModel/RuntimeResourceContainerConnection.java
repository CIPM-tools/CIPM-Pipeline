/**
 */
package dmodel.pipeline.rt.runtimeenvironment.REModel;

import org.eclipse.emf.ecore.EObject;

import org.palladiosimulator.pcm.core.entity.Entity;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Runtime Resource Container Connection</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getContainerFrom <em>Container From</em>}</li>
 *   <li>{@link dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getContainerTo <em>Container To</em>}</li>
 *   <li>{@link dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getConnectionSpecification <em>Connection Specification</em>}</li>
 * </ul>
 *
 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.REModelPackage#getRuntimeResourceContainerConnection()
 * @model
 * @generated
 */
public interface RuntimeResourceContainerConnection extends EObject, Entity {
	/**
	 * Returns the value of the '<em><b>Container From</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Container From</em>' reference.
	 * @see #setContainerFrom(RuntimeResourceContainer)
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.REModelPackage#getRuntimeResourceContainerConnection_ContainerFrom()
	 * @model required="true"
	 * @generated
	 */
	RuntimeResourceContainer getContainerFrom();

	/**
	 * Sets the value of the '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getContainerFrom <em>Container From</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Container From</em>' reference.
	 * @see #getContainerFrom()
	 * @generated
	 */
	void setContainerFrom(RuntimeResourceContainer value);

	/**
	 * Returns the value of the '<em><b>Container To</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Container To</em>' reference.
	 * @see #setContainerTo(RuntimeResourceContainer)
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.REModelPackage#getRuntimeResourceContainerConnection_ContainerTo()
	 * @model
	 * @generated
	 */
	RuntimeResourceContainer getContainerTo();

	/**
	 * Sets the value of the '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getContainerTo <em>Container To</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Container To</em>' reference.
	 * @see #getContainerTo()
	 * @generated
	 */
	void setContainerTo(RuntimeResourceContainer value);

	/**
	 * Returns the value of the '<em><b>Connection Specification</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Connection Specification</em>' reference.
	 * @see #setConnectionSpecification(ConnectionSpecification)
	 * @see dmodel.pipeline.rt.runtimeenvironment.REModel.REModelPackage#getRuntimeResourceContainerConnection_ConnectionSpecification()
	 * @model
	 * @generated
	 */
	ConnectionSpecification getConnectionSpecification();

	/**
	 * Sets the value of the '{@link dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainerConnection#getConnectionSpecification <em>Connection Specification</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Connection Specification</em>' reference.
	 * @see #getConnectionSpecification()
	 * @generated
	 */
	void setConnectionSpecification(ConnectionSpecification value);

} // RuntimeResourceContainerConnection
