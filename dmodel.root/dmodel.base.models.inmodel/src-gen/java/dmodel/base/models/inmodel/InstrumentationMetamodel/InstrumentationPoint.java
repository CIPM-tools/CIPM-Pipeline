/**
 */
package dmodel.base.models.inmodel.InstrumentationMetamodel;

import de.uka.ipd.sdq.identifier.Identifier;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Instrumentation Point</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationPoint#isActive <em>Active</em>}</li>
 * </ul>
 *
 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage#getInstrumentationPoint()
 * @model
 * @generated
 */
public interface InstrumentationPoint extends EObject, Identifier {
	/**
	 * Returns the value of the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Active</em>' attribute.
	 * @see #setActive(boolean)
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage#getInstrumentationPoint_Active()
	 * @model
	 * @generated
	 */
	boolean isActive();

	/**
	 * Sets the value of the '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationPoint#isActive <em>Active</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Active</em>' attribute.
	 * @see #isActive()
	 * @generated
	 */
	void setActive(boolean value);

} // InstrumentationPoint
