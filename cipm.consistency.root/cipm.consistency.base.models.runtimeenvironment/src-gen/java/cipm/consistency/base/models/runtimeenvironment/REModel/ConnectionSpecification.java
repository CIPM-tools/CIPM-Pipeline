/**
 */
package cipm.consistency.base.models.runtimeenvironment.REModel;

import org.eclipse.emf.ecore.EObject;

import org.palladiosimulator.pcm.core.entity.Entity;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Connection Specification</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link cipm.consistency.base.models.runtimeenvironment.REModel.ConnectionSpecification#getBandwidth <em>Bandwidth</em>}</li>
 * </ul>
 *
 * @see cipm.consistency.base.models.runtimeenvironment.REModel.REModelPackage#getConnectionSpecification()
 * @model
 * @generated
 */
public interface ConnectionSpecification extends EObject, Entity {
	/**
	 * Returns the value of the '<em><b>Bandwidth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bandwidth</em>' attribute.
	 * @see #setBandwidth(double)
	 * @see cipm.consistency.base.models.runtimeenvironment.REModel.REModelPackage#getConnectionSpecification_Bandwidth()
	 * @model
	 * @generated
	 */
	double getBandwidth();

	/**
	 * Sets the value of the '{@link cipm.consistency.base.models.runtimeenvironment.REModel.ConnectionSpecification#getBandwidth <em>Bandwidth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bandwidth</em>' attribute.
	 * @see #getBandwidth()
	 * @generated
	 */
	void setBandwidth(double value);

} // ConnectionSpecification
