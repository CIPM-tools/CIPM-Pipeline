/**
 */
package InstrumentationMetamodel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Instrumentation Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link InstrumentationMetamodel.InstrumentationModel#getPoints <em>Points</em>}</li>
 * </ul>
 *
 * @see InstrumentationMetamodel.InstrumentationModelPackage#getInstrumentationModel()
 * @model
 * @generated
 */
public interface InstrumentationModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Points</b></em>' containment reference list.
	 * The list contents are of type {@link InstrumentationMetamodel.ServiceInstrumentationPoint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Points</em>' containment reference list.
	 * @see InstrumentationMetamodel.InstrumentationModelPackage#getInstrumentationModel_Points()
	 * @model containment="true"
	 * @generated
	 */
	EList<ServiceInstrumentationPoint> getPoints();

} // InstrumentationModel
