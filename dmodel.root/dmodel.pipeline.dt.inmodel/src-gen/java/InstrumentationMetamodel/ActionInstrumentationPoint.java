/**
 */
package InstrumentationMetamodel;

import org.palladiosimulator.pcm.seff.AbstractAction;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Action Instrumentation Point</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link InstrumentationMetamodel.ActionInstrumentationPoint#getAction <em>Action</em>}</li>
 *   <li>{@link InstrumentationMetamodel.ActionInstrumentationPoint#getType <em>Type</em>}</li>
 * </ul>
 *
 * @see InstrumentationMetamodel.InstrumentationModelPackage#getActionInstrumentationPoint()
 * @model
 * @generated
 */
public interface ActionInstrumentationPoint extends InstrumentationPoint {
	/**
	 * Returns the value of the '<em><b>Action</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Action</em>' reference.
	 * @see #setAction(AbstractAction)
	 * @see InstrumentationMetamodel.InstrumentationModelPackage#getActionInstrumentationPoint_Action()
	 * @model required="true"
	 * @generated
	 */
	AbstractAction getAction();

	/**
	 * Sets the value of the '{@link InstrumentationMetamodel.ActionInstrumentationPoint#getAction <em>Action</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Action</em>' reference.
	 * @see #getAction()
	 * @generated
	 */
	void setAction(AbstractAction value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The literals are from the enumeration {@link InstrumentationMetamodel.InstrumentationType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see InstrumentationMetamodel.InstrumentationType
	 * @see #setType(InstrumentationType)
	 * @see InstrumentationMetamodel.InstrumentationModelPackage#getActionInstrumentationPoint_Type()
	 * @model required="true"
	 * @generated
	 */
	InstrumentationType getType();

	/**
	 * Sets the value of the '{@link InstrumentationMetamodel.ActionInstrumentationPoint#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see InstrumentationMetamodel.InstrumentationType
	 * @see #getType()
	 * @generated
	 */
	void setType(InstrumentationType value);

} // ActionInstrumentationPoint
