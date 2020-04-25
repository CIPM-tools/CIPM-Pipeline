/**
 */
package dmodel.base.models.inmodel.InstrumentationMetamodel;

import org.eclipse.emf.common.util.EList;

import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Service Instrumentation Point</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint#getService <em>Service</em>}</li>
 *   <li>{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint#getActionInstrumentationPoints <em>Action Instrumentation Points</em>}</li>
 * </ul>
 *
 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage#getServiceInstrumentationPoint()
 * @model
 * @generated
 */
public interface ServiceInstrumentationPoint extends InstrumentationPoint {
	/**
	 * Returns the value of the '<em><b>Service</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Service</em>' reference.
	 * @see #setService(ResourceDemandingSEFF)
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage#getServiceInstrumentationPoint_Service()
	 * @model required="true"
	 * @generated
	 */
	ResourceDemandingSEFF getService();

	/**
	 * Sets the value of the '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint#getService <em>Service</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Service</em>' reference.
	 * @see #getService()
	 * @generated
	 */
	void setService(ResourceDemandingSEFF value);

	/**
	 * Returns the value of the '<em><b>Action Instrumentation Points</b></em>' containment reference list.
	 * The list contents are of type {@link dmodel.base.models.inmodel.InstrumentationMetamodel.ActionInstrumentationPoint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Action Instrumentation Points</em>' containment reference list.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage#getServiceInstrumentationPoint_ActionInstrumentationPoints()
	 * @model containment="true"
	 * @generated
	 */
	EList<ActionInstrumentationPoint> getActionInstrumentationPoints();

} // ServiceInstrumentationPoint
