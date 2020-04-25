/**
 */
package dmodel.base.models.inmodel.InstrumentationMetamodel;

import de.uka.ipd.sdq.identifier.Identifier;

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
 *   <li>{@link dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModel#getPoints <em>Points</em>}</li>
 * </ul>
 *
 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage#getInstrumentationModel()
 * @model
 * @generated
 */
public interface InstrumentationModel extends EObject, Identifier {
	/**
	 * Returns the value of the '<em><b>Points</b></em>' containment reference list.
	 * The list contents are of type {@link dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Points</em>' containment reference list.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage#getInstrumentationModel_Points()
	 * @model containment="true"
	 * @generated
	 */
	EList<ServiceInstrumentationPoint> getPoints();

} // InstrumentationModel
